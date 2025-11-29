package com.wutsi.koki.portal.pub.user.service

import com.wutsi.koki.portal.pub.user.model.UserModel
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserHolder(
    private val service: UserService,
    private val userIdProvider: UserIdProvider,
) {
    private var model: UserModel? = null

    fun id(): Long? {
        return userIdProvider.get()
    }

    fun get(): UserModel? {
        val id = id() ?: return null

        if (model?.id == id) {
            return model
        }
        try {
            model = service.get(id)
            return model
        } catch (ex: Exception) {
            userIdProvider.remove()
            return null
        }
    }
}
