package com.wutsi.koki.portal.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.Test

class DebugRestInterceptorTest {
    private val request = mock<HttpRequest>()
    private val execution = mock<ClientHttpRequestExecution>()
    private val interceptor = DebugRestInterceptor()

    private val body = ByteArray(10)

    @Test
    fun intercept() {
        doReturn(HttpMethod.POST).whenever(request).method

        val response = mock<ClientHttpResponse>()
        doReturn(HttpStatusCode.valueOf(200)).whenever(response).statusCode
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(execution).execute(request, body)
    }
}
