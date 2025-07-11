package com.wutsi.koki.platform.tracing

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.UUID

class CookieDeviceIdProvider(private val cookieName: String) : DeviceIdProvider {
    companion object {
        const val PATH = "/"
        const val EXPIRES = 86400 * 365 * 10 // 10 years
    }

    override fun get(request: HttpServletRequest): String? {
        val cookie = getCookie(request)
        return cookie?.value
            ?: request.getAttribute(cookieName)?.toString()
            ?: UUID.randomUUID().toString()
    }

    override fun set(duid: String, request: HttpServletRequest, response: HttpServletResponse) {
        // Set in request
        request.setAttribute(cookieName, duid)

        // Return
        var cookie = getCookie(request)
        if (cookie == null) {
            cookie = Cookie(cookieName, duid)
        }

        cookie.path = PATH
        cookie.maxAge = EXPIRES
        cookie.value = duid
        response.addCookie(cookie)
    }

    private fun getCookie(request: HttpServletRequest): Cookie? =
        request.cookies?.find { it.name == cookieName }
}
