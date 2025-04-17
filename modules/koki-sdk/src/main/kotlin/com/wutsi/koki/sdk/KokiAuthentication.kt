package com.wutsi.koki.sdk

import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.dto.LoginResponse
import org.springframework.web.client.RestTemplate

class KokiAuthentication(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/auth"
    }

    fun login(request: LoginRequest): LoginResponse {
        val url = urlBuilder.build("$PATH_PREFIX/login")
        return rest.postForEntity(url, request, LoginResponse::class.java).body
    }
}
