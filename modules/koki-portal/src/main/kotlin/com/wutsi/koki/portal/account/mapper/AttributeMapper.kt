package com.wutsi.koki.portal.account.mapper

import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.portal.account.model.AttributeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import org.springframework.stereotype.Service

@Service
class AttributeMapper : TenantAwareMapper() {
    fun toAttributeModel(entity: Attribute): AttributeModel {
        val fmt = createDateFormat()
        return AttributeModel(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            label = entity.label ?: entity.name,
            required = entity.required,
            active = entity.active,
            choices = entity.choices,
            description = entity.description,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.createdAt),
        )
    }

    fun toAttributeModel(entity: AttributeSummary): AttributeModel {
        val fmt = createDateFormat()
        return AttributeModel(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            label = entity.label ?: entity.name,
            required = entity.required,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.createdAt),
        )
    }
}
