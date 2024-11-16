package com.wutsi.koki.form.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormData
import com.wutsi.koki.form.server.domain.FormDataEntity
import org.springframework.stereotype.Service

@Service
class FormDataMapper(private val objectMapper: ObjectMapper) {
    fun toFormData(entity: FormDataEntity): FormData {
        return FormData(
            id = entity.id ?: "",
            formId = entity.formId,
            data = entity.dataAsMap(objectMapper),
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            status = entity.status,
            workflowInstanceId = entity.workflowInstanceId,
        )
    }
}
