package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.security.JWTAuthentication
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.security.dto.JWTPrincipal
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserHolder(
    private val service: UserService,
    private val authenticationService: AuthenticationService,
) {
    private var model: UserModel? = null

    fun id(): Long? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is JWTAuthentication) {
            return (auth.principal as JWTPrincipal).getUserId()
        } else {
            return null
        }
    }

    fun get(): UserModel? {
        val id = id() ?: return null

        if (model?.id == id) {
            return model
        }
        try {
            model = service.user(id)
            return model
        } catch (ex: Exception) {
            authenticationService.logout()
            SecurityContextHolder.clearContext()
            return null
        }
    }
}
