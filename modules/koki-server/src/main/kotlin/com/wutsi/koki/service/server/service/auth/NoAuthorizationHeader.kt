package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.AuthorizationHeader
import org.springframework.stereotype.Service

@Service
class NoAuthorizationHeader : AuthorizationHeader {
    override fun value(service: ServiceEntity): String? {
        return null
    }
}
