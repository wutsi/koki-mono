package com.wutsi.koki.portal.security

import com.wutsi.koki.portal.rest.AccessTokenHolder
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Service

@Service
class AccessTokenAuthenticationFilter(private val accessTokenHolder: AccessTokenHolder) : Filter {
    private val jwtDecoder = JWTDecoder()

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
        val accessToken = accessTokenHolder.get(request)
        if (accessToken == null) {
            SecurityContextHolder.clearContext()
        } else {
            JWTDe
            val auth = AccessTokenAuthentication(AccessTokenPrincipal(accessToken))
            auth.isAuthenticated = true
            SecurityContextHolder.setContext(SecurityContextImpl(auth))
        }
        filterChain.doFilter(request, response)
    }
}
