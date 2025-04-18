package com.wutsi.koki.portal.auth.service

import com.wutsi.koki.portal.auth.form.LoginForm
import com.wutsi.koki.portal.security.service.AccessTokenHolder
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val kokiAuthentication: KokiAuthentication,
    private val accessTokenHolder: AccessTokenHolder,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) {
    fun login(form: LoginForm) {
        val accessToken = kokiAuthentication.login(
            request = LoginRequest(
                username = form.email,
                password = form.password,
                application = ApplicationName.PORTAL,
            )
        ).accessToken
        accessTokenHolder.set(accessToken, request, response)
    }

    fun logout() {
        accessTokenHolder.remove(request, response)
    }
}
