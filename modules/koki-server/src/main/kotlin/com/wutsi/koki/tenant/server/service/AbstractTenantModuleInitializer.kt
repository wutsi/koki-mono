package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractTenantModuleInitializer : TenantModuleInitializer {
    @Autowired
    private lateinit var registry: TenantInitializer

    @Autowired
    protected lateinit var configurationService: ConfigurationService

    @PostConstruct
    fun setUp() {
        registry.register(this)
    }

    @PreDestroy
    fun tearDown() {
        registry.unregister(this)
    }

    fun setConfigurationIfMissing(name: String, value: String, tenantId: Long) {
        val logger = LoggerFactory.getLogger(this::class.java)

        if (isNotConfigured(name, tenantId)) {
            logger.info("$name - Initialized")
            setConfiguration(name, value, tenantId)
        } else {
            logger.info("$name - Ignored")
        }
    }

    private fun setConfiguration(name: String, value: String, tenantId: Long) {
        configurationService.save(
            request = SaveConfigurationRequest(mapOf(name to value)),
            tenantId = tenantId,
        )
    }

    private fun isNotConfigured(name: String, tenantId: Long): Boolean {
        return configurationService.search(
            names = listOf(name),
            tenantId = tenantId
        ).isEmpty()
    }
}
