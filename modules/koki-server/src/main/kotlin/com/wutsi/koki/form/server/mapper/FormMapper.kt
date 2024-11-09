package com.wutsi.koki.form.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.server.domain.FormEntity
import org.springframework.stereotype.Service

@Service
class FormMapper(private val objectMapper: ObjectMapper) {
    fun toForm(entity: FormEntity): Form {
        return Form(
            id = entity.id ?: "",
            title = entity.title,
            content = objectMapper.readValue(entity.content, FormContent::class.java),
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toFormSummary(entity: FormEntity): FormSummary {
        return FormSummary(
            id = entity.id ?: "",
            title = entity.title,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
