package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.AuthorizationHeader
import org.springframework.stereotype.Service

@Service
class ApiKeyAuthorizationHeader : AuthorizationHeader {
    override fun value(service: ServiceEntity): String? {
        if (service.apiKey.isNullOrEmpty()) {
            return null
        }

        return "Bearer ${service.apiKey}"
    }
}
