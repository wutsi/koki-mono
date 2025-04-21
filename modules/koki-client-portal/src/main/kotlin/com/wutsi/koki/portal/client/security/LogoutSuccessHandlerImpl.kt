package com.wutsi.koki.portal.client.security

import com.wutsi.koki.platform.security.AccessTokenHolder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

class LogoutSuccessHandlerImpl(
    private val accessTokenHolder: AccessTokenHolder,
) : LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        accessTokenHolder.remove()
        response.sendRedirect("/login")
    }
}
