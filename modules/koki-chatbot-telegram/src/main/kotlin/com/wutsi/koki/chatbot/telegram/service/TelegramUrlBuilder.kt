package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.room.dto.RoomSummary

class UrlBuilder(
    val baseUrl: String,
    val medium: String,
) {
    fun toLocationUrl(location: Location, request: ChatbotRequest): String {
        val locationId = location.id
        val location = StringUtils.toAscii(location.name).lowercase()
        val url = "$baseUrl/l/$locationId/$location?lang=${request.language}&utm_medium=$medium"

        return url
    }

    fun toPropertyUrl(property: RoomSummary, request: ChatbotRequest): String {
        val url = "$baseUrl${property.listingUrl}?lang=${request.language}&utm_medium=$medium"
        return url
    }

    fun toViewMoreUrl(searchParameters: SearchParameters, request: ChatbotRequest, location: Location): String {
        val url = listOf(
<<<<<<< Updated upstream:modules/koki-chatbot-telegram/src/main/kotlin/com/wutsi/koki/chatbot/telegram/service/TelegramUrlBuilder.kt
            "${tenant.clientPortalUrl}/l/$locationId/$location?lang=$language&utm-medium=telegram",
            data.searchParameters.minBedrooms?.let { value -> "min-bedroom=$value" },
            data.searchParameters.maxBedrooms?.let { value -> "max-bedroom=$value" },
            data.searchParameters.propertyType?.let { value -> "type=$value" },
            data.searchParameters.leaseType?.let { value -> "lease-type=$value" },
            data.searchParameters.furnishedType?.let { value -> "furnished-type=$value" },
=======
            "$baseUrl/l/${location.id}/${StringUtils.toAscii(location.name).lowercase()}" +
                "?lang=${request.language}&utm_medium=$medium",
            searchParameters.minBedrooms?.let { value -> "min-bedroom=$value" },
            searchParameters.maxBedrooms?.let { value -> "max-bedroom=$value" },
            searchParameters.propertyType?.let { value -> "type=$value" },
            searchParameters.leaseType?.let { value -> "lease-type=$value" },
            searchParameters.furnishedType?.let { value -> "furnished-type=$value" },
            searchParameters.minBudget?.let { value -> "min-budget=$value" },
            searchParameters.maxBudget?.let { value -> "max-budget=$value" },
>>>>>>> Stashed changes:modules/koki-chatbot-telegram/src/main/kotlin/com/wutsi/koki/chatbot/UrlBuilder.kt
        )
            .filterNotNull()
            .joinToString("&")

        return url
    }
}
