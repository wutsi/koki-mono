package com.wutsi.koki.platform.tracking

import com.wutsi.koki.track.dto.ChannelType
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class CookieChannelTypeProvider(private val cookieName: String) : ChannelTypeProvider {
    companion object {
        const val PATH = "/"
        const val EXPIRES = 86400 // 1 day
    }

    override fun get(request: HttpServletRequest): ChannelType {
        val cookie = getCookie(request)
        val name = cookie?.value ?: request.getAttribute(cookieName)?.toString()

        if (name != null) {
            try {
                return ChannelType.valueOf(name.uppercase())
            } catch (ex: Exception) {
                // Ignore
            }
        }
        return ChannelType.UNKNOWN
    }

    override fun set(type: ChannelType, request: HttpServletRequest, response: HttpServletResponse) {
        // Set in request
        request.setAttribute(cookieName, type.name)

        // Return
        var cookie = getCookie(request)
        if (cookie == null) {
            cookie = Cookie(cookieName, type.name)
            cookie.path = PATH
            cookie.maxAge = EXPIRES
            response.addCookie(cookie)
        } else {
            cookie.value = type.name
        }
    }

    private fun getCookie(request: HttpServletRequest): Cookie? =
        request.cookies?.find { it.name == cookieName }
}
