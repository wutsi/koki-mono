package com.wutsi.koki.portal.service

import com.wutsi.koki.sdk.AccessTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class AccessTokenProviderImpl(
    private val accessTokenHolder: AccessTokenHolder,
    private val request: HttpServletRequest,
) : AccessTokenProvider {
    override fun accessToken(): String? {
        return accessTokenHolder.get(request)
    }
}
