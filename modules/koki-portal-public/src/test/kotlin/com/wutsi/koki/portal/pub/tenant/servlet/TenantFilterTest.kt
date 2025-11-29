package com.wutsi.koki.portal.pub.tenant.servlet

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.pub.tenant.model.TenantModel
import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class TenantFilterTest {
    private val currentTenant = mock<CurrentTenantHolder>()
    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()
    private val chain = mock<FilterChain>()
    private val filter = TenantFilter(currentTenant)
    private val tenant = TenantModel(status = TenantStatus.ACTIVE)

    @BeforeEach
    fun setUp() {
        doReturn("/foo/bar").whenever(request).requestURI
    }

    @Test
    fun filter() {
        doReturn(tenant).whenever(currentTenant).get()

        filter.doFilter(request, response, chain)

        verify(response, never()).sendError(any())
        verify(response, never()).sendRedirect(any())
        verify(chain).doFilter(request, response)
    }

    @Test
    fun suspended() {
        doReturn(tenant.copy(status = TenantStatus.SUSPENDED)).whenever(currentTenant).get()

        filter.doFilter(request, response, chain)

        verify(response, never()).sendError(any())
        verify(response).sendRedirect("/error/suspended")
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `not active`() {
        doReturn(tenant.copy(status = TenantStatus.NEW)).whenever(currentTenant).get()

        filter.doFilter(request, response, chain)

        verify(response, never()).sendError(any())
        verify(response).sendRedirect("/error/under-construction")
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `ignore errors`() {
        doReturn("/error/suspended").whenever(request).requestURI

        filter.doFilter(request, response, chain)

        verify(currentTenant, never()).get()
        verify(response, never()).sendError(any())
        verify(response, never()).sendRedirect(any())
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `ignore js`() {
        doReturn("/js/foo/koki-0.js").whenever(request).requestURI

        filter.doFilter(request, response, chain)

        verify(currentTenant, never()).get()
        verify(response, never()).sendError(any())
        verify(response, never()).sendRedirect(any())
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `ignore images`() {
        doReturn("/image/koki.png").whenever(request).requestURI

        filter.doFilter(request, response, chain)

        verify(currentTenant, never()).get()
        verify(response, never()).sendError(any())
        verify(response, never()).sendRedirect(any())
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `ignore actuator`() {
        doReturn("/actuator/health").whenever(request).requestURI

        filter.doFilter(request, response, chain)

        verify(currentTenant, never()).get()
        verify(response, never()).sendError(any())
        verify(response, never()).sendRedirect(any())
        verify(chain).doFilter(request, response)
    }
}
