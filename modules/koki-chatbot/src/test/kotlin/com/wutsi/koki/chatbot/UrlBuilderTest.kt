package com.wutsi.koki.chatbot

import com.wutsi.koki.chatbot.ai.data.SearchParameters
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

        val url = "${builder.baseUrl}/l/${location.id}/yaounde?lang=fr&utm_medium=messaging&utm_source=telegram"
        assertEquals(url, result)
    }

    @Test
    fun toPropertyUrl() {
        val result = builder.toPropertyUrl(property, request)

        val url = "${builder.baseUrl}${property.listingUrl}?lang=fr&utm_medium=messaging&utm_source=telegram"
        assertEquals(url, result)
    }

    @Test
    fun `toViewUrl - city`() {
        val params = SearchParameters(
            propertyType = "APARTMENT",
            minBedrooms = 1,
            maxBedrooms = 2,
            city = "Yaounde",
            leaseType = "SHORT_TERM",
            furnishedType = "NONE",
            minBudget = 1000.0,
            maxBudget = 2000.0,
        )

        val location = Location(id = 111, name = "Yaounde")
        val result = builder.toViewMoreUrl(params, request, location)

        val url =
            "${builder.baseUrl}/l/111/yaounde?lang=fr&utm_medium=messaging&utm_source=telegram&min-bedroom=1&max-bedroom=2&type=APARTMENT&lease-type=SHORT_TERM&furnished-type=NONE&min-budget=1000.0&max-budget=2000.0"
        assertEquals(url, result)
    }

    @Test
    fun `toViewUrl - neighborhood`() {
        val params = SearchParameters(
            city = "Yaounde",
            neighborhood = "Bastos"
        )

        val location = Location(id = 222, name = "Bastos")
        val result = builder.toViewMoreUrl(params, request, location)

        val url = "${builder.baseUrl}/l/222/bastos?lang=fr&utm_medium=messaging&utm_source=telegram"
        assertEquals(url, result)
    }
}
