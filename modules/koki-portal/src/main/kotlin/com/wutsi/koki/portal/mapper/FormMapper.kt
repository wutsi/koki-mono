package com.wutsi.koki.portal.mapper

import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.portal.model.FormModel
import org.springframework.stereotype.Service

@Service
class FormMapper {
    fun toFormModel(entity: FormSummary): FormModel {
        return FormModel(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
