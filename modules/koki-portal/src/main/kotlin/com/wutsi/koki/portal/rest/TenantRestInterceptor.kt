package com.wutsi.koki.portal.rest

import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.sdk.TenantProvider
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service

@Service
class TenantRestInterceptor(
    private val tenantProvider: TenantProvider
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers.add(HttpHeader.TENANT_ID, tenantProvider.id().toString())
        return execution.execute(request, body)
    }
}
