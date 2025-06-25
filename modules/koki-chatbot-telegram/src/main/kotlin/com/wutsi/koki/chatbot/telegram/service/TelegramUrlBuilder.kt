package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.platform.util.StringUtils
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UrlBuilder {
    fun property(property: PropertyData, tenant: TenantModel, update: Update): String {
        val language = update.message.from.languageCode
        return "${tenant.clientPortalUrl}${property.url}?lang=$language&utm-medium=telegram"
    }

    fun viewMore(data: SearchAgentData, tenant: TenantModel, update: Update): String {
        val language = update.message.from.languageCode

        val locationId = data.searchParameters.neighborhoodId ?: data.searchParameters.cityId
        val location = (data.searchParameters.neighborhood ?: data.searchParameters.city)
            ?.let { loc -> StringUtils.toAscii(loc).lowercase() }
            ?: "-"

        return listOf(
            "${tenant.clientPortalUrl}/l/${locationId}/$location?lang=$language&utm-medium=telegram",
            data.searchParameters.minBedrooms?.let { value -> "br=$value" },
            data.searchParameters.propertyType?.let { value -> "type=$value" }
        )
            .filterNotNull()
            .joinToString("&")
    }
}
