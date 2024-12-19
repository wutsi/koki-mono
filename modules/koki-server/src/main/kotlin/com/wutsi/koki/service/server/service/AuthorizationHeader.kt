package com.wutsi.koki.service.server.service

import com.wutsi.koki.service.server.domain.ServiceEntity

interface AuthorizationHeaderProvider {
    fun get(service: ServiceEntity): String?
}
