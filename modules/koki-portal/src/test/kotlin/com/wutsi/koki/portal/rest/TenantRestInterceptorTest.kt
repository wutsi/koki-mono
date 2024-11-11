package com.wutsi.koki.portal.rest

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import kotlin.test.Test
import kotlin.test.assertEquals

class TenentRestInterceptor {
    private val service = mock<TenantService>()
    private val request = mock<HttpRequest>()
    private val body = mock<ByteArray>()
    private val execution = mock<ClientHttpRequestExecution>()

    private val interceptor = TenantRestInterceptor(service)

    @Test
    fun intercept() {
        val headers = HttpHeaders()
        doReturn(headers).whenever(request).headers
        doReturn(111).whenever(service).id()

        interceptor.intercept(request, body, execution)

        assertEquals(true, headers.get(HttpHeader.TENANT_ID)?.contains("111"))
        verify(execution).execute(request, body)
    }
}
