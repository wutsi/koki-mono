package com.wutsi.koki.portal.client.error.servlet

import com.wutsi.koki.portal.client.tenant.service.CurrentTenantHolder
import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class TenantStatusFilter(private val tenantHolder: CurrentTenantHolder) : Filter {
    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        try {
            if (!shouldIgnore(req)) {
                val response = resp as HttpServletResponse

                val tenant = tenantHolder.get()
                if (tenant == null) {
                    response.sendError(404)
                } else if (tenant.status == TenantStatus.SUSPENDED) {
                    response.sendRedirect("/error/suspended")
                } else if (tenant.status != TenantStatus.ACTIVE) {
                    response.sendRedirect("/error/under-construction")
                }
            }
        } finally {
            chain.doFilter(req, resp)
        }
    }

    private fun shouldIgnore(req: ServletRequest): Boolean {
        val request = req as HttpServletRequest
        return request.requestURI.startsWith("/error") ||
            request.requestURI.startsWith("/image") ||
            request.requestURI.startsWith("/css") ||
            request.requestURI.startsWith("/js") ||
            request.requestURI.startsWith("/actuator")
    }
}
