package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.ActivityDoneEvent
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener(
    private val workflowEngine: WorkflowEngine,
    private val activityInstanceService: ActivityInstanceService,
    private val activityService: ActivityService,
    private val formDataService: FormDataService,
    private val activityRunnerProvider: ActivityRunnerProvider,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: EventPublisher,
    private val logService: LogService,
) : RabbitMQHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEventListener::class.java)
    }

    override fun handle(event: Any) {
        if (event is FormSubmittedEvent) {
            onFormSubmitted(event)
        } else if (event is FormUpdatedEvent) {
            onFormUpdated(event)
        } else if (event is ActivityDoneEvent) {
            onActivityDone(event)
        } else if (event is RunActivityCommand) {
            onRunActivityCommand(event)
        }
    }

    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onFormSubmitted - $event")
        }

        if (event.activityInstanceId != null) {
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
        }
    }

    @EventListener
    fun onFormUpdated(event: FormUpdatedEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onFormUpdated - $event")
        }

        if (event.activityInstanceId != null) {
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
        }
    }

    @EventListener
    fun onActivityDone(event: ActivityDoneEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onActivityDone - $event")
        }

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
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onRunActivityCommand - $command")
        }

        val activityInstance = activityInstanceService.get(command.activityInstanceId, command.tenantId)
        try {
            val activity = activityService.get(activityInstance.activityId)
            activityRunnerProvider.get(activity.type).run(activityInstance, workflowEngine)
        } catch (ex: Throwable) {
            logService.error(
                message = ex.message ?: "Failed",
                tenantId = activityInstance.tenantId,
                activityInstanceId = activityInstance.id,
                workflowInstanceId = activityInstance.workflowInstanceId,
                timestamp = command.timestamp,
                ex = ex,
            )
            throw ex
        }
    }

    private fun completeActivity(formDataId: String, activityInstanceId: String, tenantId: Long) {
        // Find the workflow activity
        val activityInstance = activityInstanceService.search(
            tenantId = tenantId,
            ids = listOf(activityInstanceId),
            status = WorkflowStatus.RUNNING
        ).first()

        // Complete the activity
        val formData = formDataService.get(formDataId, tenantId)
        val state = formData.dataAsMap(objectMapper)
        workflowEngine.done(activityInstance.id!!, state, tenantId)
    }
}
