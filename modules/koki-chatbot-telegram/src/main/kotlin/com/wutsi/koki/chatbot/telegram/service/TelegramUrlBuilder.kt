package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.platform.url.UrlShortener
import com.wutsi.koki.platform.util.StringUtils
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class TelegramUrlBuilder(private val urlShortener: UrlShortener) {
    fun toPropertyUrl(property: PropertyData, tenant: TenantModel, update: Update): String {
        val language = update.message.from.languageCode
        val url = "${tenant.clientPortalUrl}${property.url}?lang=$language&utm-medium=telegram"

        return urlShortener.shorten(url)
    }

    fun toViewMoreUrl(data: SearchAgentData, tenant: TenantModel, update: Update): String {
        val language = update.message.from.languageCode

        val locationId = data.searchParameters.neighborhoodId ?: data.searchParameters.cityId
        val location = (data.searchParameters.neighborhood ?: data.searchParameters.city)
            ?.let { loc -> StringUtils.toAscii(loc).lowercase() }
            ?: "-"

        val url = listOf(
            "${tenant.clientPortalUrl}/l/$locationId/$location?lang=$language&utm-medium=telegram",
            data.searchParameters.minBedrooms?.let { value -> "min-bedroom=$value" },
            data.searchParameters.maxBedrooms?.let { value -> "max-bedroom=$value" },
            data.searchParameters.propertyType?.let { value -> "type=$value" },
            data.searchParameters.leaseType?.let { value -> "lease-type=$value" },
            data.searchParameters.furnishedType?.let { value -> "furnished-type=$value" },
        )
            .filterNotNull()
            .joinToString("&")

        return urlShortener.shorten(url)
    }
}
