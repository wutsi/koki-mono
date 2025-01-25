package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.security.RequirePermissionInterceptor
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(private val currentUser: CurrentUserHolder) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequirePermissionInterceptor(currentUser))
    }
}
