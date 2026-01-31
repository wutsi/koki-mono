package com.wutsi.koki.bot.config

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KokiConfiguration(private val storageBuilder: StorageServiceBuilder) {
    @Bean
    fun storage(): StorageService {
        return storageBuilder.default()
    }
}
