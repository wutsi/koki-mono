package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.dto.event.ActivityDoneEvent
import com.wutsi.koki.workflow.dto.event.ExternalEvent
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener(
    private val workflowEngine: WorkflowEngine,
    private val workflowInstanceService: WorkflowInstanceService,
    private val formDataService: FormDataService,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: EventPublisher,
    private val logService: LogService,
    private val logger: KVLogger,
) : RabbitMQHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEventListener::class.java)
    }

    override fun handle(event: Any): Boolean {
        if (event is FormSubmittedEvent) {
            onFormSubmitted(event)
        } else if (event is ActivityDoneEvent) {
            onActivityDone(event)
        } else if (event is RunActivityCommand) {
            onRunActivityCommand(event)
        } else if (event is ExternalEvent) {
            onExternalEventReceived(event)
        } else {
            return false
        }

        logger.add("event_classname", event::class.java.simpleName)
        logger.add("listener", "WorkflowEventListener")
        return true
    }

    @EventListener
    fun onExternalEventReceived(event: ExternalEvent) {
        logger.add("event_name", event.name)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_workflow_instance_id", event.workflowInstanceId)
        logger.add("event_timestamp", event.timestamp)

        workflowEngine.received(event)
    }

    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        logger.add("event_form_id", event.formId)
        logger.add("event_form_data_id", event.formDataId)
        logger.add("event_user_id", event.userId)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_timestamp", event.timestamp)

        if (event.activityInstanceId != null) {
            logger.add("action", "activity_completed")

            val formData = formDataService.get(event.formDataId, event.tenantId)
            val state = formData.dataAsMap(objectMapper)
            workflowEngine.done(event.activityInstanceId!!, state, event.tenantId)
        } else {
            logger.add("action", "create_workflow_instance")

            val workflowInstances = workflowInstanceService.create(event)
            workflowInstances.forEach { workflowInstance ->
                try {
                    workflowEngine.start(workflowInstance.id!!, workflowInstance.tenantId)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to start WorkflowInstance#${workflowInstance.id}", ex)
                }
            }
        }
    }

    @EventListener
    fun onActivityDone(event: ActivityDoneEvent) {
        val activityInstances = workflowEngine.next(event.workflowInstanceId, event.tenantId)
        if (activityInstances.isEmpty()) {
            return
        }

        activityInstances.forEach { activityInstance ->
            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    workflowInstanceId = activityInstance.workflowInstanceId,
                    tenantId = activityInstance.tenantId
                )
            )
        }
    }

    @EventListener
    fun onRunActivityCommand(command: RunActivityCommand) {
        try {
            workflowEngine.run(command.activityInstanceId, command.tenantId)
        } catch (ex: Throwable) {
            logService.error(
                message = ex.message ?: "Failed",
                tenantId = command.tenantId,
                activityInstanceId = command.activityInstanceId,
                workflowInstanceId = command.workflowInstanceId,
                timestamp = command.timestamp,
                ex = ex,
            )
            throw ex
        }
    }
}
