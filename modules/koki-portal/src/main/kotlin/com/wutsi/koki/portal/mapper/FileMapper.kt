package com.wutsi.koki.portal.mapper

import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.portal.model.FormModel
import org.springframework.stereotype.Service
import kotlin.collections.listOf

@Service
class FormMapper {
    fun toFormModel(
        entity: FormSummary,
        workflowInstanceId: String?,
        activityInstanceId: String?
    ): FormModel {
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            viewUrl = toUrl(entity.id, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, workflowInstanceId, activityInstanceId),
        )
    }

    fun toFormModel(
        entity: Form,
        workflowInstanceId: String?,
        activityInstanceId: String?
    ): FormModel {
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            viewUrl = toUrl(entity.id, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, workflowInstanceId, activityInstanceId),
        )
    }

    private fun toUrl(
        id: String,
        readOnly: Boolean,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null
    ): String {
        return listOf(
            "/forms/$id",
            listOf(
                workflowInstanceId?.let { wid -> "workflow-instance-id=$wid" },
                activityInstanceId?.let { wid -> "activity-instance-id=$wid" },
                if (readOnly) "read-only=true" else null
            ).filterNotNull().joinToString(separator = "&")
        ).joinToString(separator = "?")
    }
}
