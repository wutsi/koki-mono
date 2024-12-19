package com.wutsi.koki.service.server.service

import com.wutsi.koki.service.server.domain.ServiceEntity

interface AuthorizationHeader {
    fun value(service: ServiceEntity): String?
}
