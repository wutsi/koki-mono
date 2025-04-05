package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName

class GeminiBuilder(
    val readTimeoutMillis: Long,
    val connectTimeoutMillis: Long,
) {
    companion object {
        val CONFIG_NAMES = listOf(
            ConfigurationName.AI_PROVIDER_GEMINI_MODEL,
            ConfigurationName.AI_PROVIDER_GEMINI_API_KEY,
        )
    }

    fun build(config: Map<String, String>): LLM {
        validate(config)
        return Gemini(
            apiKey = config[ConfigurationName.AI_PROVIDER_GEMINI_API_KEY]!!,
            model = config[ConfigurationName.AI_PROVIDER_GEMINI_MODEL]!!,
            readTimeoutMillis = readTimeoutMillis,
            connectTimeoutMillis = connectTimeoutMillis,
        )
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name -> config[name].isNullOrEmpty() }
        if (missing.isNotEmpty()) {
            throw LLMNotConfiguredException("Gemini not configured. Missing config: $missing")
        }
    }
}
