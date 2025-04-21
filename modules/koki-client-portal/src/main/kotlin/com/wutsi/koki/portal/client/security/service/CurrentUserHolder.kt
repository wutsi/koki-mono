package com.wutsi.koki.portal.client.security.service

import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.JWTAuthentication
import com.wutsi.koki.portal.client.account.model.AccountUserModel
import com.wutsi.koki.portal.client.account.service.UserService
import com.wutsi.koki.security.dto.JWTPrincipal
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserHolder(
    private val service: UserService,
    private val accessTokenHolder: AccessTokenHolder,
) {
    private var model: AccountUserModel? = null

    fun id(): Long? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is JWTAuthentication) {
            return (auth.principal as JWTPrincipal).getUserId()
        } else {
            return null
        }
    }

    fun get(): AccountUserModel? {
        val id = id() ?: return null

        if (model?.id == id) {
            return model
        }
        try {
            model = service.user(id)
            return model
        } catch (ex: Exception) {
            accessTokenHolder.remove()
            SecurityContextHolder.clearContext()
            return null
        }
    }
}
