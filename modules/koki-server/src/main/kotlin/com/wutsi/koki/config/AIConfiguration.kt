package com.wutsi.koki.config

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMHealthIndicator
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.platform.ai.llm.kimi.Kimi
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AIConfiguration(
    @param:Value("\${koki.ai.rest.read-timeout-millis}") val readTimeoutMillis: Long,
    @param:Value("\${koki.ai.rest.connect-timeout-millis}") val connectTimeoutMillis: Long,
    @param:Value("\${koki.ai.chat.type}") val chatType: String,
    @param:Value("\${koki.ai.chat.model}") val chatModel: String,
    @param:Value("\${koki.ai.chat.api-key}") val chatApiLey: String,
    @param:Value("\${koki.ai.vision.type}") val visionType: String,
    @param:Value("\${koki.ai.vision.model}") val visionModel: String,
    @param:Value("\${koki.ai.vision.api-key}") val visionApiLey: String,
) {
    companion object {
        const val CHAT_LLM_BEAN_NAME = "chat-llm"
        const val VISION_LLM_BEAN_NAME = "vision-llm"
    }

    @Bean(CHAT_LLM_BEAN_NAME)
    fun chatLLM(): LLM {
        return createLLM(chatType, chatModel, chatApiLey)
    }

    @Bean
    fun chatHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(chatLLM())
    }

    @Bean(VISION_LLM_BEAN_NAME)
    fun visionLLM(): LLM {
        return createLLM(visionType, visionModel, visionApiLey)
    }

    @Bean
    fun visionHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(visionLLM())
    }

    private fun createLLM(type: String, model: String, apiKey: String): LLM {
        when (type.lowercase()) {
            "deepseek" -> return Deepseek(
                apiKey = apiKey,
                model = model,
                readTimeoutMillis = readTimeoutMillis,
                connectTimeoutMillis = connectTimeoutMillis,
            )

            "kimi" -> return Kimi(
                apiKey = apiKey,
                model = model,
                readTimeoutMillis = readTimeoutMillis,
                connectTimeoutMillis = connectTimeoutMillis,
            )

            "gemini" -> return Gemini(
                apiKey = apiKey,
                model = model,
                readTimeoutMillis = readTimeoutMillis,
                connectTimeoutMillis = connectTimeoutMillis,
            )

            else -> throw IllegalArgumentException("LLM type $type is not supported")
        }
    }
}
