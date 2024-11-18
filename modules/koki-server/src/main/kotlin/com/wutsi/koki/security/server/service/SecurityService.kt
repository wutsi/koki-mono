package com.wutsi.koki.security.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.UnauthorizedException
import com.wutsi.koki.security.dto.JWTPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
open class SecurityService {
    fun getCurrentUserId(): Long {
        return getCurrentUserIdOrNull()
            ?: throw UnauthorizedException(Error(ErrorCode.AUTHORIZATION_UNAUTHENTICATED))
    }

    fun getCurrentUserIdOrNull(): Long? {
        val auth = SecurityContextHolder.getContext().authentication
        return if (auth == null || !auth.isAuthenticated) {
            null
        } else {
            val principal = auth.principal
            if (principal is JWTPrincipal) {
                principal.getUserId()
            } else {
                null
            }
        }
    }
}
