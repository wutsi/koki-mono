package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.ActivityDoneEvent
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener(
    private val workflowEngine: WorkflowEngine,
    private val workflowInstanceService: WorkflowInstanceService,
    private val activityInstanceService: ActivityInstanceService,
    private val activityService: ActivityService,
    private val formDataService: FormDataService,
    private val activityRunnerProvider: ActivityRunnerProvider,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: EventPublisher,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEventListener::class.java)
    }

    @Transactional
    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onFormSubmitted - $event")
        }

        if (event.activityInstanceId == null) {
            LOGGER.debug("Form[${event.formId}] is not associated with any workflow activity")
        } else {
            LOGGER.debug("Form[${event.formId}] is submitted, completing ActivityInstance[${event.activityInstanceId}]")
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
        }
    }

    @Transactional
    @EventListener
    fun onFormUpdated(event: FormUpdatedEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onFormUpdated - $event")
        }

        if (event.activityInstanceId == null) {
            LOGGER.debug("Form[${event.formId}] is not associated with any workflow activity")
        } else {
            LOGGER.debug("Form[${event.formId}] is submitted, completing ActivityInstance[${event.activityInstanceId}]")
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
        }
    }

    @Transactional
    @EventListener
    fun onActivityDone(event: ActivityDoneEvent) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onActivityDone - $event")
        }

        val activityInstance = activityInstanceService.get(event.activityInstanceId, event.tenantId)
        val workflowInstance = workflowInstanceService.get(activityInstance.workflowInstanceId, event.tenantId)
        val activityInstances = workflowEngine.next(workflowInstance)
        if (activityInstances.isEmpty()) {
            return
        }

        activityInstances.forEach { activityInstance ->
            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    tenantId = activityInstance.tenantId
                )
            )
        }
    }

    @Transactional
    @EventListener
    fun onRunActivityCommand(command: RunActivityCommand) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onActivityDone - $command")
        }

        val activityInstance = activityInstanceService.get(command.activityInstanceId, command.tenantId)
        val activity = activityService.get(activityInstance.activityId)
        activityRunnerProvider.get(activity.type).run(activityInstance, workflowEngine)
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
        workflowEngine.done(activityInstance, state)
    }
}
