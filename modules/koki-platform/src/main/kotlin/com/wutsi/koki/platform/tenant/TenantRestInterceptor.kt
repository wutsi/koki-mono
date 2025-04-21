package com.wutsi.koki.platform.tenant

import com.wutsi.koki.common.dto.HttpHeader
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class TenantRestInterceptor(
    private val tenantProvider: TenantProvider
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        tenantProvider.id()?.let { id -> request.headers.add(HttpHeader.TENANT_ID, id.toString()) }
        return execution.execute(request, body)
    }
}
