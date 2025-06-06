package com.wutsi.koki.platform.tracing.spring

import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.platform.tracing.ClientProvider
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class ClientRestInterceptor(
    private val clientProvider: ClientProvider
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers.add(HttpHeader.CLIENT_ID, clientProvider.id)
        return execution.execute(request, body)
    }
}
