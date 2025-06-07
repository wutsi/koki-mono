package com.wutsi.koki.tracking.server.config

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.local.LocalStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
@ConditionalOnProperty(
value = ["koki.storage.type"],
havingValue = "local",
)
 */
@Configuration
class LocalStorageConfiguration(
    @Value("\${koki.storage.local.directory}") private val directory: String,
    @Value("\${koki.storage.local.base-url}") private val baseUrl: String
) {
    @Bean
    fun storageService(): StorageService {
        return LocalStorageService(directory, baseUrl)
    }
}
