package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.AuthorizationHeaderProvider
import java.util.Base64

class BasicAuthorizationHeaderProvider : AuthorizationHeaderProvider {
    override fun get(service: ServiceEntity): String? {
        if (service.username.isNullOrEmpty() && service.password.isNullOrEmpty()) {
            return null
        }
        val token = (service.username ?: "") + ":" + (service.password ?: "")

        return "Basic " + Base64.getEncoder().encodeToString(token.toByteArray())
    }
}
