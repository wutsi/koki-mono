package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.GenAINotConfiguredException
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.temporal.ChronoUnit

class GeminiBuilder(
    private val restReadTimeout: Long,
    private val restConnectTimeout: Long,
) {
    companion object {
        val CONFIG_NAMES = listOf(
            ConfigurationName.AI_MODEL_GEMINI_MODEL,
            ConfigurationName.AI_MODEL_GEMINI_API_KEY,
        )
    }

    fun build(config: Map<String, String>): GenAIService {
        validate(config)
        return Gemini(
            apiKey = config[ConfigurationName.AI_MODEL_GEMINI_API_KEY]!!,
            model = config[ConfigurationName.AI_MODEL_GEMINI_MODEL]!!,
            rest = createRestTemplate()
        )
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name -> config[name].isNullOrEmpty() }
        if (missing.isNotEmpty()) {
            throw GenAINotConfiguredException("Gemini not configured. Missing config: $missing")
        }
    }

    private fun createRestTemplate(): RestTemplate {
        return RestTemplateBuilder()
            .readTimeout(Duration.of(restReadTimeout, ChronoUnit.MILLIS))
            .connectTimeout(Duration.of(restConnectTimeout, ChronoUnit.MILLIS))
            .build()
    }
}
