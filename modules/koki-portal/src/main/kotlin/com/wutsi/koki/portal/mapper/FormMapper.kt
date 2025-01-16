package com.wutsi.koki.portal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSubmission
import com.wutsi.koki.form.dto.FormSubmissionSummary
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.FormSubmissionModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.collections.listOf

@Service
class FormMapper(
    private val objectMapper: ObjectMapper,
    @Value("\${koki.webapp.base-url}") private val webappUrl: String,
) : TenantAwareMapper() {
    fun toFormModel(
        entity: FormSummary,
        workflowInstanceId: String?,
        activityInstanceId: String?,
    ): FormModel {
        val fmt = createDateTimeFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            previewUrl = toUrl(entity.id, true, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, false, workflowInstanceId, activityInstanceId),
            shareUrl = toShareUrl(entity.id),
        )
    }

    fun toFormModel(
        entity: Form,
        workflowInstanceId: String?,
        activityInstanceId: String?,
    ): FormModel {
        val fmt = createDateTimeFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity.content),
            previewUrl = toUrl(entity.id, true, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, false, workflowInstanceId, activityInstanceId),
            shareUrl = toShareUrl(entity.id),
        )
    }

    fun toUrl(
        id: String,
        readOnly: Boolean,
        preview: Boolean,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null
    ): String {
        return listOf(
            "/forms/$id", listOf(
                workflowInstanceId?.let { id -> "workflow-instance-id=$id" },
                activityInstanceId?.let { id -> "activity-instance-id=$id" },
                if (preview) "preview=true" else null,
                if (readOnly) "read-only=true" else null,
            ).filterNotNull().joinToString(separator = "&")
        ).joinToString(separator = "?")
    }

    private fun toShareUrl(id: String): String {
        return "$webappUrl/forms/$id"
    }

    fun toFormSubmissionModel(
        entity: FormSubmission,
        form: FormModel,
        submittedBy: UserModel? = null
    ): FormSubmissionModel {
        val fmt = createDateTimeFormat()
        return FormSubmissionModel(
            id = entity.id,
            data = entity.data,
            dataJSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity.data),
            form = form,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
            submittedAt = entity.submittedAt,
            submittedBy = submittedBy,
            submittedAtText = fmt.format(entity.submittedAt),
        )
    }

    fun toFormSubmissionModel(
        entity: FormSubmissionSummary,
        submittedBy: UserModel? = null
    ): FormSubmissionModel {
        val fmt = createDateTimeFormat()
        return FormSubmissionModel(
            id = entity.id,
            workflowInstanceId = entity.workflowInstanceId,
            activityInstanceId = entity.activityInstanceId,
            submittedAt = entity.submittedAt,
            submittedBy = submittedBy,
            form = FormModel(entity.formId),
            submittedAtText = fmt.format(entity.submittedAt),
        )
    }
}
