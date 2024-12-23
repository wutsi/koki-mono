package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.ActivityDoneEvent
import com.wutsi.koki.form.event.ExternalEvent
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener(
    private val workflowEngine: WorkflowEngine,
    private val activityInstanceService: ActivityInstanceService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityService: ActivityService,
    private val formDataService: FormDataService,
    private val activityRunnerProvider: ActivityRunnerProvider,
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
        } else if (event is FormUpdatedEvent) {
            onFormUpdated(event)
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
        workflowEngine.received(event)
    }

    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        logger.add("form_id", event.formId)
        logger.add("form_data_id", event.formDataId)
        logger.add("user_id", event.userId)
        logger.add("tenant_id", event.tenantId)

        if (event.activityInstanceId != null) {
            logger.add("action", "activity_completed")

            val activityInstance = completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
            logger.add("activity_instance_id", activityInstance.id)
        } else {
            logger.add("action", "create_workflow_instance")

            val workflowInstances = workflowInstanceService.create(event)
            logger.add("workflow_instance_count", workflowInstances.size)

            workflowInstances.forEach { workflowInstance ->
                try {
                    workflowEngine.start(workflowInstance.id!!, workflowInstance.tenantId)
                    logger.add("workflow_instance_id", workflowInstance.id)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to start WorkflowInstance#${workflowInstance.id}", ex)
                }
            }
        }
    }

    @EventListener
    fun onFormUpdated(event: FormUpdatedEvent) {
        if (event.activityInstanceId != null) {
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
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
        val activityInstance = activityInstanceService.get(command.activityInstanceId, command.tenantId)
        try {
            val activity = activityService.get(activityInstance.activityId)
            logger.add("activity_id", activity.id)
            logger.add("activity_type", activity.type)

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

    private fun completeActivity(
        formDataId: String,
        activityInstanceId: String,
        tenantId: Long
    ): ActivityInstanceEntity {
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

        return activityInstance
    }
}
