package com.wutsi.koki.chatbot.messenger.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.messenger.model.Message
import com.wutsi.koki.chatbot.messenger.model.Party
import com.wutsi.koki.chatbot.messenger.model.SendRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import kotlin.test.assertEquals

class MessengerClientTest {
    private val objectMapper = ObjectMapper()
    private val http = mock<HttpClient>()
    private val messenger = MessengerClient(
        objectMapper = objectMapper,
        http = http,
        apiVersion = "16.8",
        token = "123430493094",
    )

    @Test
    fun send() {
        val respone = mock<HttpResponse<String>>()
        doReturn(200).whenever(respone).statusCode()
        doReturn(respone).whenever(http).send(any<HttpRequest>(), any<BodyHandler<String>>())

        messenger.send(
            "111",
            SendRequest(
                recipient = Party(id = "ray.sponsible"),
                message = Message(text = "Yo man")
            ),
        )

        val request = argumentCaptor<HttpRequest>()
        val handler = argumentCaptor<BodyHandler<String>>()
        verify(http).send(request.capture(), handler.capture())

        assertEquals("POST", request.firstValue.method())
        assertEquals("application/json", request.firstValue.headers().firstValue("Content-Type").get())
        assertEquals("Bearer ${messenger.token}", request.firstValue.headers().firstValue("Authorization").get())
        assertEquals(
            URI.create("https://graph.facebook.com/v${messenger.apiVersion}/111/messages"),
            request.firstValue.uri()
        )
    }

    @Test
    fun error() {
        val respone = mock<HttpResponse<String>>()
        doReturn(404).whenever(respone).statusCode()
        doReturn("Failed").whenever(respone).body()
        doReturn(respone).whenever(http).send(any<HttpRequest>(), any<BodyHandler<String>>())

        assertThrows<IOException> {
            messenger.send(
                "111",
                SendRequest(
                    recipient = Party(id = "ray.sponsible"),
                    message = Message(text = "Yo man")
                ),
            )
        }
    }
}
