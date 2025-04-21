package com.wutsi.koki.platform.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class CookieAccessTokenHolder(
    private val cookieName: String,
    private val cookieTimeToLive: Int,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) : AccessTokenHolder {
    override fun set(accessToken: String) {
        val cookie = findCookie(request)
            ?: Cookie(cookieName, accessToken)

        cookie.value = accessToken
        cookie.maxAge = cookieTimeToLive
        cookie.path = "/"
        response.addCookie(cookie)
    }

    override fun remove() {
        val cookie = findCookie(request)
        if (cookie != null) {
            cookie.value = ""
            cookie.maxAge = 0
            cookie.path = "/"
            response.addCookie(cookie)
        }
    }

    override fun get(): String? {
        val cookie = findCookie(request)
        return cookie?.value
    }

    private fun findCookie(request: HttpServletRequest): Cookie? {
        return request.cookies?.find { cookie -> cookie.name == cookieName }
    }
}
