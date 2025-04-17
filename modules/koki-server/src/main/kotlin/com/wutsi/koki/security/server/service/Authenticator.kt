package com.wutsi.koki.security.server.service

import com.wutsi.koki.security.dto.LoginRequest

interface Authenticator {
    fun supports(request: LoginRequest): Boolean
    fun authenticate(request: LoginRequest, tenantId: Long): String
}
