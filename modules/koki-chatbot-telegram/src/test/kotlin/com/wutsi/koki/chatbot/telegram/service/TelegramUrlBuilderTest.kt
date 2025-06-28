package com.wutsi.koki.chatbot

import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.telegram.service.UrlBuilder
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.RoomSummary
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlBuilderTest {
    private val builder = UrlBuilder("http://localhsot:8082", "telegram")
    private val property = RoomSummary(
        listingUrl = "/rooms/1"
    )
    private val location = Location(
        id = 123,
        type = LocationType.CITY,
        name = "Yaounde",
    )
    private val request = ChatbotRequest(
        language = "fr"
    )

    @Test
    fun toLocationUrl() {
        val result = builder.toLocationUrl(location, request)

        val url = "${builder.baseUrl}/l/${location.id}/yaounde?lang=fr&utm_medium=${builder.medium}"
        assertEquals(url, result)
    }

    @Test
    fun toPropertyUrl() {
        val result = builder.toPropertyUrl(property, request)

        val url = "${builder.baseUrl}${property.listingUrl}?lang=fr&utm_medium=telegram"
        assertEquals(url, result)
    }

    @Test
    fun `toViewUrl - city`() {
<<<<<<< Updated upstream:modules/koki-chatbot-telegram/src/test/kotlin/com/wutsi/koki/chatbot/telegram/service/TelegramUrlBuilderTest.kt
        val data = SearchAgentData(
            searchParameters = SearchParameters(
                propertyType = "APARTMENT",
                minBedrooms = 1,
                maxBedrooms = 2,
                cityId = 11,
                city = "Yaounde",
                leaseType = "SHORT_TERM",
                furnishedType = "NONE"
            )
=======
        val params = SearchParameters(
            propertyType = "APARTMENT",
            minBedrooms = 1,
            maxBedrooms = 2,
            city = "Yaounde",
            leaseType = "SHORT_TERM",
            furnishedType = "NONE",
            minBudget = 1000.0,
            maxBudget = 2000.0,
>>>>>>> Stashed changes:modules/koki-chatbot-telegram/src/test/kotlin/com/wutsi/koki/chatbot/UrlBuilderTest.kt
        )

        val location = Location(id = 111, name = "Yaounde")
        val result = builder.toViewMoreUrl(params, request, location)

        val url =
<<<<<<< Updated upstream:modules/koki-chatbot-telegram/src/test/kotlin/com/wutsi/koki/chatbot/telegram/service/TelegramUrlBuilderTest.kt
            "${tenant.clientPortalUrl}/l/11/yaounde?lang=fr&utm-medium=telegram&min-bedroom=1&max-bedroom=2&type=APARTMENT&lease-type=SHORT_TERM&furnished-type=NONE"
        verify(urlShortener).shorten(url)
    }

    @Test
    fun `toViewUrl - neighborhood`() {
        val data = SearchAgentData(
            searchParameters = SearchParameters(
                cityId = 11,
                city = "Yaounde",
                neighborhoodId = 22,
                neighborhood = "Bastos",
            )
        )

        doReturn("http://bit.ly/123").whenever(urlShortener).shorten(any())

        val result = builder.toViewMoreUrl(data, tenant, update)
        assertEquals("http://bit.ly/123", result)

        val url = "${tenant.clientPortalUrl}/l/22/bastos?lang=fr&utm-medium=telegram"
        verify(urlShortener).shorten(url)
    }

    private fun createUpdate(): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", false)
        update.message.from.languageCode = "fr"
        return update
=======
            "${builder.baseUrl}/l/111/yaounde?lang=fr&utm_medium=telegram&min-bedroom=1&max-bedroom=2&type=APARTMENT&lease-type=SHORT_TERM&furnished-type=NONE&min-budget=1000.0&max-budget=2000.0"
        assertEquals(url, result)
>>>>>>> Stashed changes:modules/koki-chatbot-telegram/src/test/kotlin/com/wutsi/koki/chatbot/UrlBuilderTest.kt
    }
}
