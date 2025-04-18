package com.wutsi.koki.security.server.servlet

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.security.dto.JWTPrincipal
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.tenant.server.domain.UserEntity
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JWTAuthenticationFilterTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()
    private val filterChain = mock<FilterChain>()
    private val accessTokenService = AccessTokenService(60)

    private val filter = JWTAuthenticationFilter(accessTokenService)

    private val user = UserEntity(
        id = 11L,
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        tenantId = 1L,
    )

    private var accessToken: String? = null

    @Test
    fun anonymous() {
        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun authenticated() {
        accessToken = accessTokenService.create("foo", user.id!!, user.displayName, "USER", user.tenantId)
        doReturn("Bearer $accessToken").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        doReturn(user.tenantId.toString()).whenever(request).getHeader(HttpHeader.TENANT_ID)

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)

        val context = SecurityContextHolder.getContext()
        assertNotNull(context.authentication)
        assertTrue(context.authentication.isAuthenticated)

        val principal = context.authentication.principal as JWTPrincipal
        assertEquals(user.id, principal.getUserId())
        assertEquals(user.tenantId, principal.getTenantId())
        assertEquals(user.displayName, principal.getSubject())
    }

    @Test
    fun `no tenant in header`() {
        accessToken = accessTokenService.create("foo", user.id!!, user.displayName, "USER", user.tenantId)
        doReturn("Bearer $accessToken").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        doReturn(null).whenever(request).getHeader(HttpHeader.TENANT_ID)

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)

        val context = SecurityContextHolder.getContext()
        assertNotNull(context.authentication)
        assertTrue(context.authentication.isAuthenticated)

        val principal = context.authentication.principal as JWTPrincipal
        assertEquals(user.id, principal.getUserId())
    }

    @Test
    fun `bad tenant`() {
        accessToken = accessTokenService.create("foo", user.id!!, user.displayName, "USER", user.tenantId)
        doReturn("Bearer $accessToken").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        doReturn("11").whenever(request).getHeader(HttpHeader.TENANT_ID)

        val ex = assertThrows<AccessDeniedException> {
            filter.doFilter(request, response, filterChain)
        }
        assertEquals(ErrorCode.AUTHORIZATION_TENANT_MISMATCH, ex.message)
    }

    @Test
    fun `expired token`() {
        accessToken = AccessTokenService(-60).create("foo", user.id!!, user.displayName, "USER", user.tenantId)
        doReturn("Bearer $accessToken").whenever(request).getHeader(HttpHeaders.AUTHORIZATION)
        doReturn("11").whenever(request).getHeader(HttpHeader.TENANT_ID)

        val ex = assertThrows<AccessDeniedException> {
            filter.doFilter(request, response, filterChain)
        }
        assertEquals(ErrorCode.AUTHORIZATION_TOKEN_EXPIRED, ex.message)
    }
}
