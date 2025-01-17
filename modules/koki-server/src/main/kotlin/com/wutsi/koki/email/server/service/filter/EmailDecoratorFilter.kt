package com.wutsi.koki.email.server.service.filter

import com.wutsi.koki.email.server.service.EmailFilter
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.stereotype.Service

@Service
class EmailLayoutFilter(
    private val configurationService: ConfigurationService,
    private val tenantService: TenantService,
) : EmailFilter{
    override fun filter(html: String, tenantId: Long): String {
        
    }
}
