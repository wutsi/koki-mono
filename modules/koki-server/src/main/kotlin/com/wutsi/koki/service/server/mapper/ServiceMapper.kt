package com.wutsi.koki.service.server.mapper

import com.wutsi.koki.service.dto.Service
import com.wutsi.koki.service.dto.ServiceSummary
import com.wutsi.koki.service.server.domain.ServiceEntity

@org.springframework.stereotype.Service
class ServiceMapper {
    fun toService(entity: ServiceEntity): Service {
        return Service(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            baseUrl = entity.baseUrl,
            active = entity.active,
            authorizationType = entity.authorizationType,
            username = entity.username,
            password = entity.password,
            apiKey = entity.apiKey,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }

    fun toServiceSummary(entity: ServiceEntity): ServiceSummary {
        return ServiceSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            baseUrl = entity.baseUrl,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }
}
