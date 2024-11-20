package com.wutsi.koki.portal.security

import com.wutsi.koki.portal.service.AccessTokenHolder
import com.wutsi.koki.security.dto.JWTDecoder
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class JWTAuthenticationFilter(
    private val accessTokenHolder: AccessTokenHolder,
    private val jwtDecoder: JWTDecoder,
) : Filter {

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
            val principal = jwtDecoder.decode(accessToken)
            val auth = JWTAuthentication(principal)
            auth.isAuthenticated = true
            SecurityContextHolder.setContext(SecurityContextImpl(auth))
        }
        filterChain.doFilter(request, response)
    }
}
