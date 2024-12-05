package com.wutsi.koki.workflow.server.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.message.server.domain.MessageEntity
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.MessagingTemplateEngine
import com.wutsi.koki.platform.messaging.MessagingType
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.springframework.stereotype.Service

@Service
class SendRunner(
    private val objectMapper: ObjectMapper,
    private val userService: UserService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val configurationService: ConfigurationService,
    private val activityService: ActivityService,
    private val templateEngine: MessagingTemplateEngine,
    private val messageService: MessageService,
    logger: KVLogger
) : AbstractActivityRunner(logger) {
    override fun doRun(activityInstance: ActivityInstanceEntity, engine: WorkflowEngine) {
        if (send(activityInstance, logger)) {
            logger.add("sent", true)
            engine.done(activityInstance.id!!, emptyMap(), activityInstance.tenantId)
        } else {
            logger.add("sent", false)
        }
    }

    private fun send(activityInstance: ActivityInstanceEntity, logger: KVLogger): Boolean {
        /* Activity */
        val tenantId = activityInstance.tenantId
        val activity = activityService.get(activityInstance.activityId)
        logger.add("activity", activity.name)

        /* Message */
        val message = activity.messageId?.let { messageId -> messageService.get(messageId, tenantId) }
        if (message == null) {
            logger.add("message", "--")
            return false
        }
        logger.add("message", message.name)

        /* Send */
        val msg = createMessage(activityInstance, message, logger)
        if (msg != null) {
            createMessagingService(activityInstance.tenantId).send(msg)
            return true
        } else {
            return false
        }
    }

    private fun createMessage(
        activityInstance: ActivityInstanceEntity,
        message: MessageEntity,
        logger: KVLogger
    ): Message? {
        /* Assignee */
        val tenantId = message.tenantId
        val assignee = activityInstance.assigneeId?.let { id -> userService.get(id, tenantId) }
        if (assignee == null) {
            logger.add("assignee", "--")
            return null
        }
        logger.add("assignee", assignee.displayName)
        logger.add("assignee_email", assignee.email)

        /* Template data */
        val workflowInstance = workflowInstanceService.get(activityInstance.workflowInstanceId, tenantId)
        val data = mutableMapOf<String, Any>()
        data.putAll(workflowInstance.parametersAsMap(objectMapper))
        data.putAll(workflowInstance.stateAsMap(objectMapper))
        data.put("recipient", assignee.displayName)

        return Message(
            recipient = Party(
                displayName = assignee.displayName,
                email = assignee.email,
            ),
            subject = templateEngine.apply(message.subject, data),
            body = templateEngine.apply(message.body, data)
        )
    }

    private fun createMessagingService(tenantId: Long): MessagingService {
        val config = configurationService.search(
            names = SMTPMessagingServiceBuilder.CONFIG_NAMES,
            tenantId = tenantId
        ).map { cfg -> cfg.name to cfg.value }
            .toMap()
        return messagingServiceBuilder.build(MessagingType.EMAIL, config)
    }
}
