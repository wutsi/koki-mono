package com.wutsi.koki.platform.ai.llm

import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName

class LLMBuilder(
    private val gemini: GeminiBuilder,
    private val koki: KokiBuilder,
    private val deepseek: DeepseekBuilder
) {
    fun build(config: Map<String, String>): LLM {
        val provider = config.get(ConfigurationName.AI_PROVIDER)
        return when (provider?.uppercase()) {
            LLMType.KOKI.name -> koki.build()
            LLMType.GEMINI.name -> gemini.build(config)
            LLMType.DEEPSEEK.name -> deepseek.build(config)
            else -> throw LLMNotConfiguredException("LLM Model not supported: $provider")
        }
    }
}
