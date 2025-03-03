package com.wutsi.koki.tenant.server.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantInitializer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantInitializer::class.java)
    }

    private val moduleInitializers: MutableList<TenantModuleInitializer> = mutableListOf()

    fun register(moduleInitializer: TenantModuleInitializer) {
        LOGGER.info("Registering ${moduleInitializer::class.java.name}")

        moduleInitializers.add(moduleInitializer)
    }

    fun unregister(moduleInitializer: TenantModuleInitializer) {
        LOGGER.info("Unregistering ${moduleInitializer::class.java.name}")

        moduleInitializers.remove(moduleInitializer)
    }

    fun init(tenantId: Long) {
        moduleInitializers.forEach { initializer -> initializer.init(tenantId) }
    }
}
