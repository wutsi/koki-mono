package com.wutsi.koki.chatbot.messenger.config

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMHealthIndicator
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AIConfiguration(
    @Value("\${koki.ai.rest.read-timeout}") private val restReadTimeoutMillis: Long,
    @Value("\${koki.ai.rest.connect-timeout}") private val restConnectTimeoutMillis: Long,
    @Value("\${koki.ai.deepseek.model}") private val deepseekModel: String,
    @Value("\${koki.ai.deepseek.api-key}") private val deepseekApiKey: String,
) {
    @Bean
    fun llm(): LLM {
        return Deepseek(
            apiKey = deepseekApiKey,
            model = deepseekModel,
            connectTimeoutMillis = restConnectTimeoutMillis,
            readTimeoutMillis = restReadTimeoutMillis,
        )
    }

    @Bean
    fun deepseekHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(llm = llm())
    }
}
