package com.wutsi.koki.portal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.portal.model.FormModel
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.collections.listOf

@Service
class FormMapper(private val objectMapper: ObjectMapper) {
    fun toFormModel(
        entity: FormSummary,
        workflowInstanceId: String?,
        activityInstanceId: String?
    ): FormModel {
        val fmt = createDateFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            viewUrl = toUrl(entity.id, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, workflowInstanceId, activityInstanceId),
        )
    }

    fun toFormModel(
        entity: Form,
        workflowInstanceId: String?,
        activityInstanceId: String?
    ): FormModel {
        val fmt = createDateFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            content = objectMapper.writeValueAsString(entity.content),
            viewUrl = toUrl(entity.id, true, workflowInstanceId, activityInstanceId),
            editUrl = toUrl(entity.id, false, workflowInstanceId, activityInstanceId),
        )
    }

    fun toUrl(
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

    private fun createDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }
}
