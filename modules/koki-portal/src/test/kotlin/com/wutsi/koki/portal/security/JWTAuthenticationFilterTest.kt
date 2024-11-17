package com.wutsi.koki.portal.security

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.rest.AccessTokenHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessTokenAuthenticationFilterTest {
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()
    private val chain = mock<FilterChain>()

    private val holder = mock<AccessTokenHolder>()
    private val filter = AccessTokenAuthenticationFilter(holder)
    private val accessToken = "111"

    @Test
    fun authenticated() {
        doReturn(accessToken).whenever(holder).get(request)

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertTrue(auth is AccessTokenAuthentication)
        assertEquals(accessToken, auth.name)
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
