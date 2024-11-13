package com.wutsi.koki.security.server.servlet

import com.auth0.jwt.exceptions.TokenExpiredException
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.security.server.service.JWTAuthentication
import com.wutsi.koki.security.server.service.JWTPrincipal
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Service

@Service
class JWTAuthenticationFilter(private val authenticationService: AuthenticationService) : Filter {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        doFilterInternal(request as HttpServletRequest, response as HttpServletResponse, filterChain)
    }

    private fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = getAccessToken(request)
        if (accessToken == null) {
            SecurityContextHolder.clearContext()
        } else {
            try {
                val principal = authenticationService.decodeAccessToken(accessToken)
                validate(principal, request)

                val auth = JWTAuthentication(principal)
                auth.isAuthenticated = true
                SecurityContextHolder.setContext(SecurityContextImpl(auth))
            } catch (ex: TokenExpiredException) {
                throw AccessDeniedException(ErrorCode.AUTHORIZATION_TOKEN_EXPIRED, ex)
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun getAccessToken(request: HttpServletRequest): String? {
        val value = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }

    private fun validate(principal: JWTPrincipal, request: HttpServletRequest) {
        val tenantId = request.getHeader(HttpHeader.TENANT_ID)
        if (tenantId != null && !tenantId.equals(principal.getTenantId().toString())) {
            throw AccessDeniedException(ErrorCode.AUTHORIZATION_TENANT_MISMATCH)
        }
    }
}
