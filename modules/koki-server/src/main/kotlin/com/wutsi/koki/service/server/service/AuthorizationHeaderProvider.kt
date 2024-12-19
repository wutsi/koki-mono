package com.wutsi.koki.service.server.service

import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.server.service.auth.ApiKeyAuthorizationHeader
import com.wutsi.koki.service.server.service.auth.BasicAuthorizationHeader
import com.wutsi.koki.service.server.service.auth.NoAuthorizationHeader
import org.springframework.stereotype.Service

@Service
class AuthorizationHeaderProvider(
    private val basic: BasicAuthorizationHeader,
    private val apiKey: ApiKeyAuthorizationHeader,
    private val none: NoAuthorizationHeader,
) {
    fun get(type: AuthorizationType): AuthorizationHeader {
        return when (type) {
            AuthorizationType.API_KEY -> apiKey
            AuthorizationType.BASIC -> basic
            else -> none
        }
    }
}
