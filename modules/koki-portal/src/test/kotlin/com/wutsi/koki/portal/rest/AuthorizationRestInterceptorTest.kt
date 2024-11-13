package com.wutsi.koki.portal.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.Test

class AuthorizationRestInterceptorTest {
    private val headers = mock<HttpHeaders>()
    private val request = mock<HttpRequest>()
    private val response = mock<ClientHttpResponse>()
    private val execution = mock<ClientHttpRequestExecution>()

    private val httpRequest = mock<HttpServletRequest>()
    private val accessTokenHolder = mock<AccessTokenHolder>()
    private val interceptor = AuthorizationInterceptor(accessTokenHolder, httpRequest)

    private val body = ByteArray(10)

    @Test
    fun intercept() {
        val accessToken = "121232"
        doReturn(accessToken).whenever(accessTokenHolder).get(httpRequest)

        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers).add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        verify(execution).execute(request, body)
    }

    @Test
    fun anonymous() {
        doReturn(null).whenever(accessTokenHolder).get(httpRequest)

        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers, never()).add(eq(HttpHeaders.AUTHORIZATION), any())
        verify(execution).execute(request, body)
    }
}
