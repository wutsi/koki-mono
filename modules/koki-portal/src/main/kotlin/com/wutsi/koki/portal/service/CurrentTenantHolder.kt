package com.wutsi.koki.portal.service

import com.wutsi.koki.tenant.dto.TenantModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import java.net.URI

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentTenantHolder(
    private val service: TenantService,
    private val request: HttpServletRequest,
) {
    private var model: TenantModel? = null

    fun get(): TenantModel? {
        if (model == null) {
            val host = URI(request.requestURL.toString()).host
            model = service.tenants().find { tenant -> tenant.domainName == host }
        }
        return model!!
    }
}
