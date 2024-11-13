package com.wutsi.koki.sdk

import com.wutsi.koki.party.dto.LoginRequest
import com.wutsi.koki.party.dto.LoginResponse
import org.springframework.web.client.RestTemplate

class KokiAuthentication(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val PATH_PREFIX = "/v1/auth"
    }

    fun login(email: String, password: String): LoginResponse {
        val url = urlBuilder.build("$PATH_PREFIX/login")
        val request = LoginRequest(
            email = email,
            password = password,
        )
        return rest.postForEntity(url, request, LoginResponse::class.java).body
    }
}
