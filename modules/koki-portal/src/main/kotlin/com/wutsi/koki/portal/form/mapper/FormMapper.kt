package com.wutsi.koki.portal.form.mapper

import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.portal.form.model.FormModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.service.Moment
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class FormMapper(private val moment: Moment) : TenantAwareMapper() {
    fun toFormModel(
        entity: Form,
        users: Map<Long, UserModel>,
    ): FormModel {
        val fmt = createDateTimeFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[entity.id] },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedAtMoment = moment.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[entity.id] },
        )
    }

    fun toFormModel(
        entity: FormSummary,
        users: Map<Long, UserModel>,
    ): FormModel {
        val fmt = createDateTimeFormat()
        return FormModel(
            id = entity.id,
            name = entity.name,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdAtMoment = moment.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[entity.id] },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedAtMoment = moment.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[entity.id] },
        )
    }
}
