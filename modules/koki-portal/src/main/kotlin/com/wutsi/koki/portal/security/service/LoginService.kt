package com.wutsi.koki.portal.security.service

import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.portal.security.form.LoginForm
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val kokiAuthentication: KokiAuthentication,
    private val accessTokenHolder: AccessTokenHolder,
) {
    fun login(form: LoginForm) {
        val accessToken = kokiAuthentication.login(
            request = LoginRequest(
                username = form.username,
                password = form.password,
                application = ApplicationName.PORTAL,
            )
        ).accessToken
        accessTokenHolder.set(accessToken)
    }

    fun logout() {
        accessTokenHolder.remove()
    }
}
