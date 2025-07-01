package com.wutsi.koki.chatbot.messenger.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.chatbot.messenger.AbstractTest
import com.wutsi.koki.chatbot.messenger.model.Entry
import com.wutsi.koki.chatbot.messenger.model.Event
import com.wutsi.koki.chatbot.messenger.model.Messaging
import com.wutsi.koki.chatbot.messenger.model.Party
import com.wutsi.koki.chatbot.messenger.service.MessengerConsumer
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.RestTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class WebhookEventEndpointTest : AbstractTest() {
    @MockitoBean
    private lateinit var consumer: MessengerConsumer

    @Test
    fun onEvent() {
        val messaging1 = Messaging(sender = Party("111"))
        val messaging2 = Messaging(sender = Party("222"))
        val event = Event(
            `object` = "page",
            entry = listOf(
                Entry(
                    messaging = listOf(messaging1, messaging2)
                )
            )
        )

        val response = RestTemplate().postForEntity("http://localhost:$port/webhook", event, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(consumer).consume(messaging1)
        verify(consumer).consume(messaging2)
    }
}
