package com.wutsi.koki.portal.pub.user.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class UserIdProvider(
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) {
    companion object {
        const val COOKIE_NAME = "__wuid"
        const val COOKIE_TIME_TO_LIVE = 86400 // 1 day
    }

    fun set(userId: Long) {
        val value = userId.toString()
        val cookie = findCookie(request)
            ?: Cookie(COOKIE_NAME, value)

        cookie.value = value
        cookie.maxAge = COOKIE_TIME_TO_LIVE
        cookie.path = "/"
        response.addCookie(cookie)
    }

    fun remove() {
        val cookie = findCookie(request)
        if (cookie != null) {
            cookie.value = ""
            cookie.maxAge = 0
            cookie.path = "/"
            response.addCookie(cookie)
        }
    }

    fun get(): Long? {
        val cookie = findCookie(request)
        return cookie?.value?.toLongOrNull()
    }

    private fun findCookie(request: HttpServletRequest): Cookie? {
        return request.cookies?.find { cookie -> cookie.name == COOKIE_NAME }
    }
}
