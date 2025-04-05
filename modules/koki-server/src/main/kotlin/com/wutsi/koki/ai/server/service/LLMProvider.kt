package com.wutsi.koki.ai.server.service

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service

@Service
class LLMProvider(
    private val configurationService: ConfigurationService,
    private val llmBuilder: LLMBuilder,
) {
    fun get(tenantId: Long): LLM {
        val configs = configurationService.search(keyword = "ai.", tenantId = tenantId)
            .map { config -> config.name to config.value }
            .toMap()

        return llmBuilder.build(configs)
    }
}
