package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.platform.url.UrlShortener
import org.mockito.Mockito.mock
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.message.Message
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramUrlBuilderTest {
    private val urlShortener = mock<UrlShortener>()
    private val builder = TelegramUrlBuilder(urlShortener)
    private val tenant = TenantModel(
        clientPortalUrl = "http://localhsot:8082"
    )
    private val property = PropertyData(
        url = "/rooms/1"
    )
    private val update = createUpdate()

    @Test
    fun toPropertyUrl() {
        doReturn("http://bit.ly/123").whenever(urlShortener).shorten(any())

        val result = builder.toPropertyUrl(property, tenant, update)

        assertEquals("http://bit.ly/123", result)

        val url = "${tenant.clientPortalUrl}${property.url}?lang=fr&utm-medium=telegram"
        verify(urlShortener).shorten(url)
    }

    @Test
    fun `toViewUrl - city`() {
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
        )

        doReturn("http://bit.ly/123").whenever(urlShortener).shorten(any())

        val result = builder.toViewMoreUrl(data, tenant, update)
        assertEquals("http://bit.ly/123", result)

        val url =
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
    }
}
