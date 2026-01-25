package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.springframework.stereotype.Service

@Service
class StorageProvider(
    private val configurationService: ConfigurationService,
    private val storageBuilder: StorageServiceBuilder,
) {
    fun get(tenantId: Long): StorageService {
        val configs = configurationService.search(
            tenantId = tenantId, keyword = "storage."
        ).associate { config -> config.name to config.value }
        return storageBuilder.build(configs)
    }
}
