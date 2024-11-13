package com.wutsi.koki.security.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
open class SecurityService {
    fun getCurrentUserId(): Long {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || !auth.isAuthenticated) {
            throw UnauthorizedException(Error(ErrorCode.AUTHORIZATION_UNAUTHENTICATED))
        } else {
            val principal = auth.principal
            if (principal is JWTPrincipal) {
                return principal.getUserId()
            } else {
                throw UnauthorizedException(Error(ErrorCode.AUTHORIZATION_SCHEME_NOT_SUPPORTED))
            }
        }
    }
}
