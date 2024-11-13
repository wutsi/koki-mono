package com.wutsi.koki.portal.rest

import com.wutsi.koki.portal.rest.AuthenticationService.Companion.COOKIE_ACCESS_TOKEN
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service

@Service
class AuthorizationInterceptor(private val httpRequest: HttpServletRequest) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val accessToken = getAccessToken()
        if (accessToken != null) {
            request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }
        return execution.execute(request, body)
    }

    fun getAccessToken(): String? {
        val cookie = httpRequest.cookies.find { cookie -> cookie.name == COOKIE_ACCESS_TOKEN }
        return cookie?.value
    }

}
