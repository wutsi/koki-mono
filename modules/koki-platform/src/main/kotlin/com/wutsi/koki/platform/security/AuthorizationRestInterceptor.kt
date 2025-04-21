package com.wutsi.koki.platform.security

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class AuthorizationRestInterceptor(
    private val accessTokenHolder: AccessTokenHolder,
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val accessToken = accessTokenHolder.get()
        if (accessToken != null) {
            request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }
        return execution.execute(request, body)
    }
}
