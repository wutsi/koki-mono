package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ServiceModel
import com.wutsi.koki.service.dto.Service
import com.wutsi.koki.service.dto.ServiceSummary
import java.text.SimpleDateFormat

@org.springframework.stereotype.Service
class ServiceMapper {
    fun toServiceModel(entity: ServiceSummary): ServiceModel {
        val fmt = createDateFormat()
        return ServiceModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toServiceModel(entity: Service): ServiceModel {
        val fmt = createDateFormat()
        return ServiceModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            username = entity.username,
            password = entity.password,
            apiKey = entity.apiKey,
            baseUrl = entity.baseUrl,
            authorizationType = entity.authorizationType,
        )
    }

    private fun createDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }
}
