package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.service.storage.StorageService
import com.wutsi.koki.portal.service.storage.local.LocalStorageService
import com.wutsi.koki.portal.service.storage.local.LocalStorageServlet
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LocalStorageConfiguration(
    @Value("\${koki.storage.local.directory}") private val directory: String,
    @Value("\${koki.webapp.base-url}") private val baseUrl: String,
    @Value("\${koki.storage.local.servlet-path}") private val servletPath: String,
) {
    @Bean
    fun storageService(): StorageService {
        return LocalStorageService(directory, "$baseUrl$servletPath")
    }

    @Bean
    open fun storageServlet(): ServletRegistrationBean<*> =
        ServletRegistrationBean(LocalStorageServlet(directory), "$servletPath/*")
}
