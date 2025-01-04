package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.account.dto.Attribute
import com.wutsi.koki.account.dto.AttributeSummary
import com.wutsi.koki.account.server.domain.AttributeEntity
import org.springframework.stereotype.Service

@Service
class AttributeMapper {
    fun toAttribute(entity: AttributeEntity) = Attribute(
        id = entity.id!!,
        name = entity.name,
        type = entity.type,
        label = entity.label,
        required = entity.required,
        active = entity.active,
        choices = entity.choices
            ?.trim()
            ?.ifEmpty { null }
            ?.split("\n")?.toList() ?: emptyList(),
        description = entity.description,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt
    )

    fun toAttributeSummary(entity: AttributeEntity) = AttributeSummary(
        id = entity.id!!,
        name = entity.name,
        type = entity.type,
        label = entity.label,
        required = entity.required,
        active = entity.active,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt
    )
}
