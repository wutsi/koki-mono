package com.wutsi.koki.portal.client.tenant.service

import com.wutsi.koki.portal.tenant.model.TenantModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentTenantHolder(
    private val service: TenantService,
    private val request: HttpServletRequest,
) {
    private var model: TenantModel? = null

    fun get(): TenantModel? {
        if (model == null) {
            val url = request.requestURL.toString()
            model = service.tenants().find { tenant ->
                tenant.clientPortalUrl != null && url.startsWith(tenant.clientPortalUrl)
            }
        }
        return model
    }
}
