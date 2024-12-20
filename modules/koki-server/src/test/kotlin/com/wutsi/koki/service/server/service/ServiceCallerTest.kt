package com.wutsi.koki.service.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.server.domain.ServiceEntity
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceCallerTest {
    private val rest = mock<RestTemplate>()
    private val auth = mock<AuthorizationHeader>()
    private val authProvider = mock<AuthorizationHeaderProvider>()
    private val caller = ServiceCaller(rest, ObjectMapper(), authProvider)
    private val service = ServiceEntity(
        baseUrl = "https://api.paypal.com",
        authorizationType = AuthorizationType.NONE
    )
    private val body = mapOf("id" to "111")

    @BeforeEach
    fun setUp() {
        doReturn(null).whenever(auth).value(any())
        doReturn(auth).whenever(authProvider).get(any())

        val response = ResponseEntity<Map<String, Any>>(body, HttpStatus.OK)
        doReturn(response).whenever(rest)
            .exchange(any<URI>(), any<HttpMethod>(), any<HttpEntity<String>>(), eq(Any::class.java))
    }

    @Test
    fun post() {
        caller.call(
            service = service,
            method = HttpMethod.POST,
            path = "/v1/capture",
            input = mapOf("x" to "xx", "y" to "yy"),
        )

        val entity = argumentCaptor<HttpEntity<String>>()
        verify(rest).exchange(
            eq(URI("https://api.paypal.com/v1/capture")),
            eq(HttpMethod.POST),
            entity.capture(),
            eq(Any::class.java)
        )

        assertEquals(MediaType.APPLICATION_JSON, entity.firstValue.headers.contentType)
        assertEquals(null, entity.firstValue.headers["Authorization"])
        assertEquals(null, entity.firstValue.headers["X-Workflow-Instance-ID"])
        assertEquals("{\"x\":\"xx\",\"y\":\"yy\"}", entity.firstValue.body)
    }

    @Test
    fun get() {
        doReturn("Basic xxx:yyyy").whenever(auth).value(any())

        caller.call(
            service = service,
            method = HttpMethod.GET,
            workflowInstanceId = "1111-2222",
        )

        val entity = argumentCaptor<HttpEntity<String>>()
        verify(rest).exchange(
            eq(URI("https://api.paypal.com")),
            eq(HttpMethod.GET),
            entity.capture(),
            eq(Any::class.java)
        )

        assertEquals(MediaType.APPLICATION_JSON, entity.firstValue.headers.contentType)
        assertEquals("Basic xxx:yyyy", entity.firstValue.headers["Authorization"]?.get(0))
        assertEquals("1111-2222", entity.firstValue.headers["X-Workflow-Instance-ID"]?.get(0))
        assertEquals(null, entity.firstValue.body)
    }
}
