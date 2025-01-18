package com.wutsi.koki.portal.tenant.servlet

import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantFilter(private val currentTenant: CurrentTenantHolder) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantFilter::class.java)
    }

    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        try {
            if (!shouldIgnore(req)) {
                val response = resp as HttpServletResponse

                // Get tenant
                val tenant = currentTenant.get()

                // Check status
                if (tenant == null) {
                    LOGGER.error("No tenant found")
                    response.sendError(404)
                } else if (tenant.status == TenantStatus.SUSPENDED) {
                    LOGGER.error("Tenant#${tenant.name} is suspended")
                    response.sendRedirect("/error/suspended")
                } else if (tenant.status != TenantStatus.ACTIVE) {
                    LOGGER.error("Tenant#${tenant.name} is not active")
                    response.sendError(404)
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
            request.requestURI.startsWith("/js")
    }
}
