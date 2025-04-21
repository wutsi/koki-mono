package com.wutsi.koki.portal.client.security.service

import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.portal.client.security.form.LoginForm
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val accessTokenHolder: AccessTokenHolder,
    private val koki: KokiAuthentication,
) {
    fun login(form: LoginForm) {
        val accessToken = koki.login(
            request = LoginRequest(
                username = form.username,
                password = form.password,
                application = ApplicationName.CLIENT,
            )
        ).accessToken
        accessTokenHolder.set(accessToken)
    }

    fun logout() {
        accessTokenHolder.remove()
    }
}
