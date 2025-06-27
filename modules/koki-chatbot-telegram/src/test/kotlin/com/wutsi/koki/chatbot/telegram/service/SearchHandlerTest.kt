package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.agent.SearchAgent
import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.telegram.AbstractTest
import com.wutsi.koki.chatbot.telegram.RoomFixtures
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchHandlerTest : AbstractTest() {
    private val agent = mock<SearchAgent>()

    @MockitoBean
    private lateinit var agentFactory: AgentFactory

    @MockitoBean
    private lateinit var client: TelegramClient

    @MockitoBean
    private lateinit var telegramUrlBuilder: TelegramUrlBuilder

    @Autowired
    private lateinit var handler: SearchHandler

    val data = SearchAgentData(
        properties = listOf(
            PropertyData(
                id = 1,
                url = "/rooms/1",
                pricePerMonth = 1550.0,
                currency = "CAD",
                bathrooms = 2,
                bedrooms = 5,
            ),
            PropertyData(
                id = 2,
                url = "/rooms/2",
                pricePerNight = 550.0,
                currency = "CAD",
                bathrooms = 0,
                bedrooms = 1,
            ),
            PropertyData(
                id = 3,
                url = "/rooms/3",
                pricePerNight = 950.0,
                currency = "CAD",
                bathrooms = 4,
                bedrooms = 5,
            )
        ),
        searchParameters = SearchParameters(
            neighborhoodId = 11L,
            neighborhood = "Bastos",
            cityId = 22L,
            city = "Yaounde",
            propertyType = RoomType.APARTMENT.name,
            minBedrooms = 1,
            maxBedrooms = 3,
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(agent).whenever(agentFactory).crateSearchAgent()
        doReturn(objectMapper.writeValueAsString(data)).whenever(agent).run(any())
    }

    @Test
    fun search() {
        val url0 = "https://bit.ly/1"
        doReturn(url0).whenever(telegramUrlBuilder).toViewMoreUrl(any(), any(), any())

        val url1 = "https://bit.ly/1"
        val url2 = "https://bit.ly/2"
        val url3 = "https://bit.ly/3"
        doReturn(url1)
            .doReturn(url2)
            .doReturn(url3)
            .whenever(telegramUrlBuilder).toPropertyUrl(any(), any(), any())

        val update = createUpdate("/search Im looking for apartment in Yaounde", language = "en")
        handler.handle(update)
        Thread.sleep(1000)

        verify(agent).run("Im looking for apartment in Yaounde")

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(data.properties.size + 2)).execute(msg.capture())
        assertEquals(SearchHandler.SEARCHING, msg.firstValue.text)
        assertEquals(true, msg.secondValue.text.contains(url1))
        assertEquals(true, msg.thirdValue.text.contains(url2))
        assertEquals(true, msg.allValues[3].text.contains(url3))
        assertNotNull(msg.allValues[4].replyMarkup)

        verify(telegramUrlBuilder).toPropertyUrl(eq(data.properties[0]), any(), eq(update))
        verify(telegramUrlBuilder).toPropertyUrl(eq(data.properties[1]), any(), eq(update))
        verify(telegramUrlBuilder).toPropertyUrl(eq(data.properties[2]), any(), eq(update))
        verify(telegramUrlBuilder).toViewMoreUrl(eq(data), any(), eq(update))

        val track = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(track.capture())
        assertEquals("1|2|3", track.firstValue.track.productId)
        assertEquals(update.message.from.id.toString(), track.firstValue.track.deviceId)
        assertEquals(null, track.firstValue.track.accountId)
        assertEquals(1L, track.firstValue.track.tenantId)
        assertEquals(ChannelType.MESSAGING, track.firstValue.track.channelType)
        assertEquals("telegram", track.firstValue.track.page)
        assertEquals(TrackEvent.IMPRESSION, track.firstValue.track.event)
        assertEquals(null, track.firstValue.track.referrer)
        assertEquals(null, track.firstValue.track.ip)
        assertEquals(null, track.firstValue.track.ua)
        assertEquals(null, track.firstValue.track.value)
        assertEquals(null, track.firstValue.track.url)
    }

    @Test
    fun `few recommendations`() {
        val xdata = data.copy(
            searchParameters = data.searchParameters,
            properties = listOf(data.properties[0])
        )
        doReturn(objectMapper.writeValueAsString(xdata)).whenever(agent).run(any())

        val url1 = "https://bit.ly/1"
        doReturn(url1).whenever(telegramUrlBuilder).toPropertyUrl(any(), any(), any())

        val update = createUpdate("/search Im looking for apartment in Yaounde", language = "en")
        handler.handle(update)
        Thread.sleep(1000)

        verify(agent).run("Im looking for apartment in Yaounde")

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(xdata.properties.size + 1)).execute(msg.capture())
        assertEquals(SearchHandler.SEARCHING, msg.firstValue.text)
        assertEquals(true, msg.secondValue.text.contains(url1))

        verify(telegramUrlBuilder).toPropertyUrl(eq(data.properties[0]), any(), eq(update))
        verify(telegramUrlBuilder, never()).toViewMoreUrl(any(), any(), any())

        val track = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(track.capture())
        assertEquals("1", track.firstValue.track.productId)
        assertEquals(update.message.from.id.toString(), track.firstValue.track.deviceId)
        assertEquals(null, track.firstValue.track.accountId)
        assertEquals(1L, track.firstValue.track.tenantId)
        assertEquals(ChannelType.MESSAGING, track.firstValue.track.channelType)
        assertEquals("telegram", track.firstValue.track.page)
        assertEquals(TrackEvent.IMPRESSION, track.firstValue.track.event)
        assertEquals(null, track.firstValue.track.referrer)
        assertEquals(null, track.firstValue.track.ip)
        assertEquals(null, track.firstValue.track.ua)
        assertEquals(null, track.firstValue.track.value)
        assertEquals(null, track.firstValue.track.url)
    }

    @Test
    fun empty() {
        val xdata = data.copy(
            searchParameters = data.searchParameters,
            properties = emptyList()
        )
        doReturn(objectMapper.writeValueAsString(xdata)).whenever(agent).run(any())

        val update = createUpdate("/search house from Paris", language = "en")
        handler.handle(update)
        Thread.sleep(1000)

        verify(agent).run("house from Paris")

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(2)).execute(msg.capture())
        assertEquals(SearchHandler.SEARCHING, msg.firstValue.text)
        assertEquals(SearchHandler.NOT_FOUND, msg.secondValue.text)
        assertEquals(RoomFixtures.metrics.size, (msg.secondValue.replyMarkup as InlineKeyboardMarkup).keyboard.size)

        verify(telegramUrlBuilder, never()).toPropertyUrl(any(), any(), any())
        verify(telegramUrlBuilder, never()).toViewMoreUrl(any(), any(), any())
        verify(publisher, never()).publish(any())
    }

    private fun createUpdate(text: String, language: String = "en"): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", false)
        update.message.from.languageCode = language
        update.message.chat = Chat(123, "channel")
        update.message.text = text

        if (text.startsWith("/")) {
            val i = text.indexOf(" ")
            val cmd = if (i > 0) text.substring(0, i) else text
            val msg = MessageEntity("bot_command", 0, cmd.length)
            msg.text = cmd
            update.message.entities = listOf(msg)
        }
        return update
    }
}
