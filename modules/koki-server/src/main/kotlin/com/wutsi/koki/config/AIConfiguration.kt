package com.wutsi.koki.config

import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.platform.ai.llm.LLMHealthIndicator
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.deepseek.DeepseekBuilder
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AIConfiguration(
    @Value("\${koki.ai.rest.read-timeout}") private val restReadTimeoutMillis: Long,
    @Value("\${koki.ai.rest.connect-timeout}") private val restConnectTimeoutMillis: Long,
    @Value("\${koki.ai.model.type}") private val model: String,
    @Value("\${koki.ai.model.gemini.model}") private val geminiModel: String,
    @Value("\${koki.ai.model.gemini.api-key}") private val geminiApiKey: String,
    @Value("\${koki.ai.model.deepseek.model}") private val deepseekModel: String,
    @Value("\${koki.ai.model.deepseek.api-key}") private val deepseekApiKey: String,
) {
    @Bean
    fun LLMBuilder(): LLMBuilder {
        return LLMBuilder(
            gemini = geminiBuilder(),
            koki = kokiBuilder(),
            deepseek = deepseekBuilder(),
        )
    }

    @Bean
    fun kokiBuilder(): KokiBuilder {
        return KokiBuilder(
            type = LLMType.valueOf(model.uppercase()),
            geminiApiKey = geminiApiKey,
            geminiModel = geminiModel,
            geminiBuilder = geminiBuilder(),
            deepseekApiKey = deepseekApiKey,
            deepseekModel = deepseekModel,
            deepseekBuilder = deepseekBuilder(),
        )
    }

    @Bean
    fun geminiBuilder(): GeminiBuilder {
        return GeminiBuilder(
            connectTimeoutMillis = restConnectTimeoutMillis,
            readTimeoutMillis = restReadTimeoutMillis,
        )
    }

    @Bean
    fun deepseekBuilder(): DeepseekBuilder {
        return DeepseekBuilder(
            connectTimeoutMillis = restConnectTimeoutMillis,
            readTimeoutMillis = restReadTimeoutMillis,
        )
    }

    @Bean
    fun geminiHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(
            llm = Gemini(
                apiKey = geminiApiKey,
                model = geminiModel,
            )
        )
    }

    @Bean
    fun deepseekHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(
            llm = Gemini(
                apiKey = deepseekApiKey,
                model = deepseekModel,
            )
        )
    }
}
