package com.wutsi.koki.config

import com.wutsi.koki.platform.ai.genai.GenAIServiceBuilder
import com.wutsi.koki.platform.ai.genai.gemini.Gemini
import com.wutsi.koki.platform.ai.genai.gemini.GeminiBuilder
import com.wutsi.koki.platform.ai.genai.gemini.GeminiHealthIndicator
import com.wutsi.koki.platform.ai.genai.koki.KokiBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class GenAIConfiguration(
    @Value("\${koki.genai.rest.read-timeout}") private val restReadTimeout: Long,
    @Value("\${koki.genai.rest.connect-timeout}") private val restConnectTimeout: Long,
    @Value("\${koki.genai.gemini.model}") private val geminiModel: String,
    @Value("\${koki.genai.gemini.api-key}") private val geminiApiKey: String,
) {
    @Bean
    fun genAIServiceBuilder(): GenAIServiceBuilder {
        return GenAIServiceBuilder(
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
