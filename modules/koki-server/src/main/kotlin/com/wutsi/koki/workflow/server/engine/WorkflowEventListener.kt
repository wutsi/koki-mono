package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener(
    private val workflowEngine: WorkflowEngine,
    private val activityInstanceService: ActivityInstanceService,
    private val formDataService: FormDataService,
    private val objectMapper: ObjectMapper
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEventListener::class.java)
    }

    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        LOGGER.debug("onFormSubmitted - $event")

        if (event.activityInstanceId == null) {
            LOGGER.debug("Form[${event.formId}] is not associated with any workflow activity")
        } else {
            LOGGER.debug("Form[${event.formId}] is submitted, completing ActivityInstance[${event.activityInstanceId}]")
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
        }
    }

    @EventListener
    fun onFormUpdated(event: FormUpdatedEvent) {
        LOGGER.debug("onFormUpdated - $event")

        if (event.activityInstanceId == null) {
            LOGGER.debug("Form[${event.formId}] is not associated with any workflow activity")
        } else {
            LOGGER.debug("Form[${event.formId}] is submitted, completing ActivityInstance[${event.activityInstanceId}]")
            completeActivity(event.formDataId, event.activityInstanceId!!, event.tenantId)
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
        workflowEngine.done(activityInstance, state)
    }
}
