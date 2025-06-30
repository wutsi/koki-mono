package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotResponse
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.telegram.AbstractTest
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramConsumerTest : AbstractTest() {
    @MockitoBean
    private lateinit var telegram: TelegramClient

    @MockitoBean
    private lateinit var chatbot: Chatbot

    @Autowired
    private lateinit var consumer: TelegramConsumer

    @Autowired
    private lateinit var messages: MessageSource

    private val chatId: Long = 444L
    private val userId: Long = 777L

    @Test
    fun `invalid query`() {
        doThrow(InvalidQueryException::class).whenever(chatbot).process(any())

        val update = createUpdate("yo")
        consumer.consume(listOf(update))

        val msg = argumentCaptor<SendMessage>()
        verify(telegram).execute(msg.capture())
        assertEquals(
            messages.getMessage("chatbot.help", arrayOf("Canada"), Locale("en")),
            msg.firstValue.text
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun error() {
        doThrow(RuntimeException::class).whenever(chatbot).process(any())

        val update = createUpdate("yo")
        consumer.consume(listOf(update))

        val msg = argumentCaptor<SendMessage>()
        verify(telegram).execute(msg.capture())
        assertEquals(
            messages.getMessage("chatbot.error", arrayOf(), Locale("en")),
            msg.firstValue.text
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `not found`() {
        doReturn(ChatbotResponse()).whenever(chatbot).process(any())

        val update = createUpdate("yo")
        consumer.consume(listOf(update))

        val msg = argumentCaptor<SendMessage>()
        verify(telegram).execute(msg.capture())
        assertEquals(
            messages.getMessage("chatbot.not_found", arrayOf(), Locale("en")),
            msg.firstValue.text
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `room found`() {
        val rooms = listOf(
            RoomSummary(id = 111, listingUrl = "/rooms/1", numberOfRooms = 1, pricePerMonth = Money(1600.0, "CAD")),
            RoomSummary(
                id = 222,
                listingUrl = "/rooms/2",
                numberOfRooms = 1,
                numberOfBathrooms = 1,
                pricePerNight = Money(80.0, "CAD")
            ),
            RoomSummary(
                id = 333,
                listingUrl = "/rooms/3",
                numberOfRooms = 2,
                numberOfBathrooms = 1,
                pricePerNight = Money(85.0, "CAD")
            )
        )
        doReturn(
            ChatbotResponse(
                rooms = rooms,
                searchParameters = SearchParameters(),
                searchLocation = Location(id = 111, name = "Yaounde")
            )
        ).whenever(chatbot).process(any())

        val update = createUpdate("yo")
        consumer.consume(listOf(update))

        val msg = argumentCaptor<SendMessage>()
        verify(telegram, times(rooms.size + 1)).execute(msg.capture())

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals("telegram", event.firstValue.track.page)
        assertEquals("111|222|333", event.firstValue.track.productId)
        assertEquals(userId.toString(), event.firstValue.track.deviceId)
        assertEquals(1L, event.firstValue.track.tenantId)
    }

    private fun createUpdate(text: String, language: String = "en"): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(userId, "Ray Sponsible", false)
        update.message.from.languageCode = language
        update.message.chat = Chat(chatId, "channel")
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
