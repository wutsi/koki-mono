package com.wutsi.koki.portal.client.config

import com.wutsi.koki.portal.client.security.RequiresModuleInterceptor
import com.wutsi.koki.portal.client.tenant.service.CurrentTenantHolder
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(private val currentTenant: CurrentTenantHolder) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequiresModuleInterceptor(currentTenant))
    }
}
