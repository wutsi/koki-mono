package com.wutsi.koki.form.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormSubmission
import com.wutsi.koki.form.dto.FormSubmissionSummary
import com.wutsi.koki.form.server.domain.FormSubmissionEntity
import org.springframework.stereotype.Service

@Service
class FormSubmissionMapper(private val objectMapper: ObjectMapper) {
    fun toFormSubmission(entity: FormSubmissionEntity): FormSubmission {
        return FormSubmission(
            id = entity.id ?: "",
            formId = entity.formId,
            data = entity.dataAsMap(objectMapper),
            submittedAt = entity.submittedAt,
            submittedById = entity.submittedById,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
        )
    }

    fun toFormSubmissionSummary(entity: FormSubmissionEntity): FormSubmissionSummary {
        return FormSubmissionSummary(
            id = entity.id ?: "",
            formId = entity.formId,
            submittedAt = entity.submittedAt,
            submittedById = entity.submittedById,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
        )
    }
}
