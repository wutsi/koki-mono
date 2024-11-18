package com.wutsi.koki.portal.rest

import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.portal.security.JWTAuthentication
import com.wutsi.koki.security.dto.JWTPrincipal
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserHolder(
    private val service: UserService
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

        model = service.get(id)
        return model
    }
}
