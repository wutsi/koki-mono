package com.wutsi.koki.translation.server.service

import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.platform.translation.TranslationService
import com.wutsi.koki.platform.translation.TranslationServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service

@Service
class TranslationServiceProvider(
    private val configurationService: ConfigurationService,
    private val translationBuilder: TranslationServiceBuilder,
) {
    fun get(tenantId: Long): TranslationService {
        val config = configurationService.search(tenantId = tenantId, keyword = "translation.")
            .map { cfg -> cfg.name to cfg.value }
            .toMap()
            .toMutableMap()

        if (config[ConfigurationName.TRANSLATION_PROVIDER] == TranslationProvider.AI.name) {
            config.putAll(
                configurationService.search(tenantId = tenantId, keyword = "ai.")
                    .map { cfg -> cfg.name to cfg.value }
                    .toMap()
            )
        }

        return translationBuilder.build(config)
    }
}
