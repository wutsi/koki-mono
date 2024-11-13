package com.wutsi.koki.portal.rest

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service

@Service
class AuthorizationInterceptor(
    private val accessTokenHolder: AccessTokenHolder,
    private val httpRequest: HttpServletRequest,
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val accessToken = accessTokenHolder.get(httpRequest)
        if (accessToken != null) {
            request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }
        return execution.execute(request, body)
    }
}
