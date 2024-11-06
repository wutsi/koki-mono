package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.server.domain.AttributeEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.stereotype.Service

@Service
class ConfigurationMapper(private val attributeMapper: AttributeMapper) {
    fun toConfiguration(entity: ConfigurationEntity, attribute: AttributeEntity? = null) = Configuration(
        id = entity.id!!,
        attribute = attributeMapper.toAttribute(attribute ?: entity.attribute),
        value = entity.value,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt
    )
}
