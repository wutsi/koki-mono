package com.wutsi.koki.portal.rest

import com.wutsi.koki.portal.page.form.LoginForm
import com.wutsi.koki.sdk.KokiAuthentication
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val kokiAuthentication: KokiAuthentication,
    private val accessTokenHolder: AccessTokenHolder,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) {
    fun login(form: LoginForm) {
        val accessToken = kokiAuthentication.login(form.email, form.password).accessToken
        accessTokenHolder.set(accessToken, request, response)
    }
}
