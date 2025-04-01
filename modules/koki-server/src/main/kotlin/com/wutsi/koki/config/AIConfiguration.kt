package com.wutsi.koki.config

import com.wutsi.koki.platform.ai.llm.LLMBuilder
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.platform.ai.llm.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.llm.gemini.GeminiHealthIndicator
import com.wutsi.koki.platform.ai.llm.koki.KokiBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AIConfiguration(
    @Value("\${koki.ai.rest.read-timeout}") private val restReadTimeout: Long,
    @Value("\${koki.ai.rest.connect-timeout}") private val restConnectTimeout: Long,
    @Value("\${koki.ai.gemini.model}") private val geminiModel: String,
    @Value("\${koki.ai.gemini.api-key}") private val geminiApiKey: String,
) {
    @Bean
    fun LLMBuilder(): LLMBuilder {
        return LLMBuilder(
            gemini = geminiBuilder(),
            koki = kokiBuilder(),
        )
    }

    @Bean
    fun kokiBuilder(): KokiBuilder {
        return KokiBuilder(
            apiKey = geminiApiKey,
            model = geminiModel,
            delegate = geminiBuilder(),
        )
    }

    @Bean
    fun geminiBuilder(): GeminiBuilder {
        return GeminiBuilder(
            restConnectTimeout = restConnectTimeout,
            restReadTimeout = restReadTimeout,
        )
    }

    @Bean
    fun geminiHealthCheck(): HealthIndicator {
        return GeminiHealthIndicator(
            gemini = Gemini(
                apiKey = geminiApiKey,
                model = geminiModel,
                rest = RestTemplate(),
            )
        )
    }
}
