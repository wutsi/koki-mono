package com.wutsi.koki.form.server.mapper

import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.server.domain.FormEntity
import org.springframework.stereotype.Service

@Service
class FormMapper {
    fun toForm(entity: FormEntity): Form {
        return Form(
            id = entity.id!!,
            code = entity.code,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
        )
    }

    fun toFormSummary(entity: FormEntity): FormSummary {
        return FormSummary(
            id = entity.id!!,
            code = entity.code,
            name = entity.name,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
        )
    }
}
