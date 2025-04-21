package com.wutsi.koki.platform.security

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.AuthorizationHttpRequestInterceptor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.Test

class AuthorizationHttpRequestInterceptorTest {
    private val headers = mock<HttpHeaders>()
    private val request = mock<HttpRequest>()
    private val response = mock<ClientHttpResponse>()
    private val execution = mock<ClientHttpRequestExecution>()

    private val accessTokenHolder = mock<AccessTokenHolder>()
    private val interceptor = AuthorizationHttpRequestInterceptor(accessTokenHolder)

    private val body = ByteArray(10)

    @Test
    fun intercept() {
        val accessToken = "121232"
        doReturn(accessToken).whenever(accessTokenHolder).get()

        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers).add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        verify(execution).execute(request, body)
    }

    @Test
    fun anonymous() {
        doReturn(null).whenever(accessTokenHolder).get()

        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers, never()).add(eq(HttpHeaders.AUTHORIZATION), any())
        verify(execution).execute(request, body)
    }
}
