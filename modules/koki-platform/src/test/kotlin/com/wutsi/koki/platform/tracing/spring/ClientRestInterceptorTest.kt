package com.wutsi.koki.platform.tracing.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.platform.tracing.ClientProvider
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.collections.firstOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.toByteArray

class ClientRestInterceptorTest {
    private val request = mock<HttpRequest>()
    private val response = mock<ClientHttpResponse>()
    private val execution = mock<ClientHttpRequestExecution>()
    private val body = "foo".toByteArray()
    private val provider = ClientProvider("yo")
    private val interceptor = ClientRestInterceptor(provider)

    @Test
    fun intercept() {
        val headers = HttpHeaders()
        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        val result = interceptor.intercept(request, body, execution)

        assertEquals(response, result)
        assertEquals(provider.id, headers.get(HttpHeader.CLIENT_ID)?.firstOrNull())

        verify(execution).execute(request, body)
    }
}
