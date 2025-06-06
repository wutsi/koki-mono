package com.wutsi.koki.platform.tracing.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.platform.tracing.HeaderDeviceIdProvider
import jakarta.servlet.http.HttpServletRequest
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceIdRestInterceptorTest {
    private val request = mock<HttpRequest>()
    private val response = mock<ClientHttpResponse>()
    private val execution = mock<ClientHttpRequestExecution>()
    private val body = "foo".toByteArray()
    private val provider = mock<HeaderDeviceIdProvider>()
    private val req = mock<HttpServletRequest>()
    private val interceptor = DeviceIdRestInterceptor(provider, req)

    @Test
    fun intercept() {
        val headers = HttpHeaders()
        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())
        doReturn("123").whenever(provider).get(req)

        val result = interceptor.intercept(request, body, execution)

        assertEquals(response, result)
        assertEquals("123", headers.get(HttpHeader.DEVICE_ID)?.firstOrNull())

        verify(execution).execute(request, body)
    }

    @Test
    fun `no id`() {
        val headers = HttpHeaders()
        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())
        doReturn(null).whenever(provider).get(req)

        val result = interceptor.intercept(request, body, execution)

        assertEquals(response, result)
        assertEquals(null, headers.get(HttpHeader.DEVICE_ID)?.firstOrNull())

        verify(execution).execute(request, body)
    }
}
