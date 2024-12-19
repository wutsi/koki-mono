package com.wutsi.koki.service.server.service.auth

import com.wutsi.koki.service.server.domain.ServiceEntity
import com.wutsi.koki.service.server.service.AuthorizationHeaderProvider

class BasicAuthorizationHeaderProvider : AuthorizationHeaderProvider {
    override fun get(service: ServiceEntity): String? {
        return null
    }
}
