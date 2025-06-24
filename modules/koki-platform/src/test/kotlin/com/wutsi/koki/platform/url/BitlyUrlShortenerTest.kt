package com.wutsi.koki.platform.url

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.Mockito.mock
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class BitlyUrlShortenerTest {
    @Test
    fun shorten() {
        val service = BitlyUrlShortener(System.getenv("BITLY_API_KEY"), ObjectMapper())

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://bit.ly/2KPbcAE", short)
    }

    @Test
    fun invalidToken() {
        val service = BitlyUrlShortener("xxxx", ObjectMapper())

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://www.google.ca", short)
    }

    @Test
    fun serviceError() {
        val http = mock<HttpClient>()
        doThrow(RuntimeException::class).whenever(http).send(any<HttpRequest>(), any<HttpResponse.BodyHandler<*>>())

        val service = BitlyUrlShortener(System.getenv("BITLY_API_KEY"), ObjectMapper(), http)

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://www.google.ca", short)
    }
}
