package com.wutsi.koki.chatbot.messenger.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotResponse
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.messenger.AbstractTest
import com.wutsi.koki.chatbot.messenger.RoomFixtures.rooms
import com.wutsi.koki.chatbot.messenger.model.Message
import com.wutsi.koki.chatbot.messenger.model.Messaging
import com.wutsi.koki.chatbot.messenger.model.Party
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class MessengerConsumerTest : AbstractTest() {
    @MockitoBean
    private lateinit var messenger: MessengerClient

    @MockitoBean
    private lateinit var chatbot: Chatbot

    @Autowired
    private lateinit var consumer: MessengerConsumer

    @Autowired
    private lateinit var messages: MessageSource

    private val senderId = "444"
    private val recipientId = "777"

    @Test
    fun `invalid query`() {
        doThrow(InvalidQueryException::class).whenever(chatbot).process(any())

        val update = createMessaging("yo")
        consumer.consume(update)

        val text = argumentCaptor<String>()
        verify(messenger, times(2)).send(eq(update.recipient.id), eq(update.sender.id), text.capture())
        assertEquals(
            messages.getMessage("chatbot.processing", arrayOf(), Locale("en")),
            text.firstValue
        )
        assertEquals(
            messages.getMessage("chatbot.help", arrayOf("Canada"), Locale("en")),
            text.secondValue
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun error() {
        doThrow(RuntimeException::class).whenever(chatbot).process(any())

        val update = createMessaging("yo")
        consumer.consume(update)

        val text = argumentCaptor<String>()
        verify(messenger, times(2)).send(eq(update.recipient.id), eq(update.sender.id), text.capture())
        assertEquals(
            messages.getMessage("chatbot.processing", arrayOf(), Locale("en")),
            text.firstValue
        )
        assertEquals(
            messages.getMessage("chatbot.error", arrayOf(), Locale("en")),
            text.secondValue
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `not found`() {
        doReturn(ChatbotResponse()).whenever(chatbot).process(any())

        val update = createMessaging("yo")
        consumer.consume(update)

        val text = argumentCaptor<String>()
        verify(messenger, times(2)).send(eq(update.recipient.id), eq(update.sender.id), text.capture())
        assertEquals(
            messages.getMessage("chatbot.processing", arrayOf(), Locale("en")),
            text.firstValue
        )
        assertEquals(
            messages.getMessage("chatbot.not_found", arrayOf(), Locale("en")),
            text.secondValue
        )

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `room found`() {
        doReturn(
            ChatbotResponse(
                rooms = rooms,
                searchParameters = SearchParameters(),
                searchLocation = Location(id = 111, name = "Yaounde")
            )
        ).whenever(chatbot).process(any())

        val update = createMessaging("yo")
        consumer.consume(update)

        val text = argumentCaptor<String>()
        verify(messenger, times(rooms.size + 1)).send(eq(update.recipient.id), eq(update.sender.id), text.capture())
        assertEquals(
            messages.getMessage("chatbot.processing", arrayOf(), Locale("en")),
            text.firstValue
        )
        assertEquals(
            true,
            text.secondValue.contains(rooms[0].listingUrl!!, true)
        )
        assertEquals(
            true,
            text.thirdValue.contains(rooms[1].listingUrl!!, true)
        )

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals("messenger", event.firstValue.track.page)
        assertEquals(rooms.map { it.id }.joinToString("|"), event.firstValue.track.productId)
        assertEquals(update.sender.id, event.firstValue.track.deviceId)
        assertEquals(1L, event.firstValue.track.tenantId)
        assertEquals(update.timestamp, event.firstValue.track.time)
    }

    private fun createMessaging(text: String?): Messaging {
        return Messaging(
            timestamp = System.currentTimeMillis(),
            sender = Party(id = senderId),
            recipient = Party(id = recipientId),
            message = Message(text = text)
        )
    }
}
