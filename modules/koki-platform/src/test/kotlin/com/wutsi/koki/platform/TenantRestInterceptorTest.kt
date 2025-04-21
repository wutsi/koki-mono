package com.wutsi.koki.platform

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.Test

class TenantRestInterceptorTest {
    private val provider = mock<TenantProvider>()
    private val headers = mock<HttpHeaders>()
    private val request = mock<HttpRequest>()
    private val response = mock<ClientHttpResponse>()
    private val execution = mock<ClientHttpRequestExecution>()

    private val body = ByteArray(10)
    private val interceptor = TenantRestInterceptor(provider)

    @Test
    fun intercept() {
        doReturn(111L).whenever(provider).id()
        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers).add(HttpHeader.TENANT_ID, "111")
        verify(execution).execute(request, body)
    }

    @Test
    fun noTenant() {
        doReturn(null).whenever(provider).id()
        doReturn(headers).whenever(request).headers
        doReturn(response).whenever(execution).execute(any(), any())

        interceptor.intercept(request, body, execution)

        verify(headers, never()).add(any(), any())
        verify(execution).execute(request, body)
    }
}
