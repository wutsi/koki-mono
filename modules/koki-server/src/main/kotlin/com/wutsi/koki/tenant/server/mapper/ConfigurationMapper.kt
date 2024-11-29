package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.stereotype.Service

@Service
class ConfigurationMapper {
    fun toConfiguration(entity: ConfigurationEntity) = Configuration(
        name = entity.name,
        value = entity.value,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt
    )
}
