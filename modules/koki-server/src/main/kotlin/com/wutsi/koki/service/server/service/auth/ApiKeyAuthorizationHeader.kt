package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.AuthorizationHeaderProvider

class ApiKeyAuthorizationHeaderProvider : AuthorizationHeaderProvider {
    override fun get(service: ServiceEntity): String? {
        if (service.apiKey.isNullOrEmpty()) {
            return null
        }

        return "Bearer ${service.apiKey}"
    }
}
