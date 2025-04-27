package com.wutsi.koki.platform.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

class RequestAccessTokenHolder(
    private val request: HttpServletRequest,
) : AccessTokenHolder {
    override fun set(accessToken: String) {
    }

    override fun remove() {
    }

    override fun get(): String? {
        val value = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }
}
