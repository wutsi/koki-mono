package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class EmailDecoratorService(
    private val templatingEngine: TemplatingEngine,
    private val configurationService: ConfigurationService,
    private val tenantService: TenantService,
) {
    fun get(tenantId: Long): String {
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(ConfigurationName.EMAIL_DECORATOR)
        )
        return if (configs.isEmpty()) {
            IOUtils.toString(EmailDecoratorService::class.java.getResourceAsStream("/email/decorator.html"), "utf-8")
        } else {
            configs[0].value
        }
    }

    fun decorate(body: String, tenantId: Long): String {
        val tenant = tenantService.get(tenantId)
        val context = mapOf(
            "tenant_name" to tenant.name,
            "tenant_portal_url" to tenant.portalUrl,
            "tenant_logo_url" to tenant.logoUrl,
            "tenant_icon_url" to tenant.iconUrl,
            "tenant_website_url" to tenant.websiteUrl,
            "body" to body,
        )
            .filterValues { value -> value != null }
            .mapValues { entry -> entry.value as String }
        val layout = get(tenantId)
        return templatingEngine.apply(layout, context)
    }
}
