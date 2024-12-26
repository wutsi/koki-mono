package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.MessagingType
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.event.ActivityStartedEvent
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.springframework.stereotype.Service

@Service
class NotificationEventListener(
    private val tenantService: TenantService,
    private val userService: UserService,
    private val activityInstanceService: ActivityInstanceService,
    private val activityService: ActivityService,
    private val workflowService: WorkflowService,
    private val templateEngine: TemplatingEngine,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val logger: KVLogger
) : RabbitMQHandler {
    override fun handle(event: Any): Boolean {
        if (event is ActivityStartedEvent) {
            if (!onActivityStarted(event)) {
                return false
            }
        } else {
            return false
        }

        logger.add("event_classname", event::class.java.simpleName)
        logger.add("listener", "NotificationEventListener")
        return true
    }

    private fun onActivityStarted(event: ActivityStartedEvent): Boolean {
        val activityInstance = activityInstanceService.get(event.activityInstanceId, event.tenantId)
        if (activityInstance.assigneeId == null) { // No assignee, Ignore it
            return false
        }

        val activity = activityService.get(activityInstance.activityId)
        if (!canComplete(activity)) { // Is it an interactive activity
            return false
        }

        val workflow = workflowService.get(activity.workflowId, event.tenantId)
        val assignee = userService.get(activityInstance.assigneeId!!, event.tenantId)
        val tenant = tenantService.get(event.tenantId)
        val message = createActivityStartedMessage(activityInstance, activity, workflow, assignee, tenant)
        createMessagingService(event.tenantId).send(message)
        return true
    }

    private fun canComplete(activity: ActivityEntity): Boolean {
        return activity.type == ActivityType.USER || activity.type == ActivityType.MANUAL
    }

    private fun createActivityStartedMessage(
        activityInstance: ActivityInstanceEntity,
        activity: ActivityEntity,
        workflow: WorkflowEntity,
        assignee: UserEntity,
        tenant: TenantEntity
    ): Message {
        val data = mutableMapOf<String, Any>()
        data.put("activity_id", activityInstance.id!!)
        data.put("activity_title", (activity.title ?: activity.name))
        data.put("workflow_title", (workflow.title ?: workflow.name))
        data.put("action_url", "${tenant.portalUrl}/tasks/${activityInstance.id}")

        val subject = "You've got a new task - {{activity_title}}"
        val body = """
            <p>
                You've been assigned the task <b>{{activity_title}}</b> of the process <b>{{workflow_title}}</b>
            </p>
            <hr>
            <p>
                <a class="btn btn-primary" href='{{action_url}}'>Complete the Task</a>
            </p>
            <p class='text-smaller'>
                You are receiving this email because you have been assigned as participant of the process
            </p>
        """.trimIndent()
        return Message(
            recipient = Party(email = assignee.email, displayName = assignee.displayName),
            subject = templateEngine.apply(subject, data),
            body = templateEngine.apply(body, data)
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
