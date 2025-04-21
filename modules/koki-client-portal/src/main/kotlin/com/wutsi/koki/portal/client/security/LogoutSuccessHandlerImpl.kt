package com.wutsi.koki.portal.client.security

import com.wutsi.koki.portal.client.security.service.LoginService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Service

@Service
class LogoutSuccessHandlerImpl(
    private val loginService: LoginService
) : LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        loginService.logout()
        response.sendRedirect("/login")
    }
}
