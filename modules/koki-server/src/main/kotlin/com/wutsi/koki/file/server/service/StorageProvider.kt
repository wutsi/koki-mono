package com.wutsi.koki.file.server.service

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service

@Service
class StorageProvider(
    private val configurationService: ConfigurationService,
    private val storageBuilder: StorageServiceBuilder,
) {
    fun get(tenantId: Long): StorageService {
        val configs = configurationService.search(
            tenantId = tenantId, keyword = "storage."
        ).map { config -> config.name to config.value }.toMap()
        return storageBuilder.build(configs)
    }
}
