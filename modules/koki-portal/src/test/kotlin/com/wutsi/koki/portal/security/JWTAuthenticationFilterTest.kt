package com.wutsi.koki.portal.security

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.rest.AccessTokenHolder
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.JWTPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JWTAuthenticationFilterTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()
    private val chain = mock<FilterChain>()

    private val holder = mock<AccessTokenHolder>()
    private val decoder = mock<JWTDecoder>()
    private val filter = JWTAuthenticationFilter(holder, decoder)
    private val accessToken = "111"

    @Test
    fun authenticated() {
        // GIVEN
        doReturn(accessToken).whenever(holder).get(request)

        val principal = mock<JWTPrincipal>()
        doReturn(principal).whenever(decoder).decode(any())

        // WHEN
        filter.doFilter(request, response, chain)

        // THEN
        val auth = SecurityContextHolder.getContext().authentication
        assertTrue(auth is JWTAuthentication)
        assertTrue(auth.isAuthenticated)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun anonymous() {
        doReturn(null).whenever(holder).get(request)

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)

        verify(chain).doFilter(request, response)
    }
}
