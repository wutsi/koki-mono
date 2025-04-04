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
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class AIConfiguration(
    @Value("\${koki.ai.rest.read-timeout}") private val restReadTimeout: Long,
    @Value("\${koki.ai.rest.connect-timeout}") private val restConnectTimeout: Long,
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
            restConnectTimeout = restConnectTimeout,
            restReadTimeout = restReadTimeout,
        )
    }

    @Bean
    fun deepseekBuilder(): DeepseekBuilder {
        return DeepseekBuilder(
            restConnectTimeout = restConnectTimeout,
            restReadTimeout = restReadTimeout,
        )
    }

    @Bean
    fun geminiHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(
            llm = Gemini(
                apiKey = geminiApiKey,
                model = geminiModel,
                rest = createRestTemplate(),
            )
        )
    }

    @Bean
    fun deepseekHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(
            llm = Gemini(
                apiKey = deepseekApiKey,
                model = deepseekModel,
                rest = createRestTemplate(),
            )
        )
    }

    private fun createRestTemplate(): RestTemplate {
        return RestTemplateBuilder()
            .readTimeout(Duration.of(restReadTimeout, ChronoUnit.MILLIS))
            .connectTimeout(Duration.of(restConnectTimeout, ChronoUnit.MILLIS))
            .build()
    }
}
