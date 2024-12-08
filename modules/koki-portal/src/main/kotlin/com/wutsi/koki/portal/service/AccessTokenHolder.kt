package com.wutsi.koki.portal.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class AccessTokenHolder {
    companion object {
        const val COOKIE_ACCESS_TOKEN = "__atk"
        const val TTL = 86400
    }

    fun set(accessToken: String, request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookie(request)
            ?: Cookie(COOKIE_ACCESS_TOKEN, accessToken)

        cookie.value = accessToken
        cookie.maxAge = TTL
        cookie.path = "/"
        response.addCookie(cookie)
    }

    fun remove(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookie(request)
        if (cookie != null) {
            cookie.value = ""
            cookie.maxAge = 0
            cookie.path = "/"
            response.addCookie(cookie)
        }
    }

    fun get(request: HttpServletRequest): String? {
        val cookie = findCookie(request)
        return cookie?.value
    }

    private fun findCookie(request: HttpServletRequest): Cookie? {
        return request.cookies?.find { cookie -> cookie.name == COOKIE_ACCESS_TOKEN }
    }
}
