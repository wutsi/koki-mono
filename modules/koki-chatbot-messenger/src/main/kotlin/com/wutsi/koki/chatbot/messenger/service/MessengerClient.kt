package com.wutsi.koki.chatbot.messenger.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class MessengerClient(
    private val objectMapper: ObjectMapper,
    private val http: HttpClient,

    @Value("\${koki.messenger.page-id}") val pageId: Long,
    @Value("\${koki.messenger.token}") val token: String,
    @Value("\${koki.messenger.api-version}") val apiVersion: String,
) {
    private val url = "https://graph.facebook.com/v$apiVersion/$pageId/messages"

    fun send(recipientId: String, text: String) {
        val payload = mapOf(
            "messaging_type" to "RESPONSE",
            "recipient" to mapOf("id" to recipientId),
            "message" to mapOf("text" to text),
        )
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload)
                )
            )
            .build()
        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw IOException("Failure: ${response.statusCode()} - ${response.body()}")
        }
    }
}
