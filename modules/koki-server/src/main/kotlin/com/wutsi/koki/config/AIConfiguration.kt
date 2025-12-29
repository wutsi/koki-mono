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
    @param:Value("\${koki.ai.chat.type}") val chatType: String,
    @param:Value("\${koki.ai.chat.model}") val chatModel: String,
    @param:Value("\${koki.ai.chat.api-key}") val chatApiLey: String,
    @param:Value("\${koki.ai.chat.rest.read-timeout-millis}") val chatReadTimeoutMillis: Long,
    @param:Value("\${koki.ai.chat.rest.connect-timeout-millis}") val chatConnectTimeoutMillis: Long,

    @param:Value("\${koki.ai.chat-with-tools.type}") val chatWithToolsType: String,
    @param:Value("\${koki.ai.chat-with-tools.model}") val chatWithToolsModel: String,
    @param:Value("\${koki.ai.chat-with-tools.api-key}") val chatWithToolsApiLey: String,
    @param:Value("\${koki.ai.chat-with-tools.rest.read-timeout-millis}") val chatWithToolsReadTimeoutMillis: Long,
    @param:Value("\${koki.ai.chat-with-tools.rest.connect-timeout-millis}") val chatWithToolsConnectTimeoutMillis: Long,

    @param:Value("\${koki.ai.vision.type}") val visionType: String,
    @param:Value("\${koki.ai.vision.model}") val visionModel: String,
    @param:Value("\${koki.ai.vision.api-key}") val visionApiLey: String,
    @param:Value("\${koki.ai.vision.rest.read-timeout-millis}") val visionReadTimeoutMillis: Long,
    @param:Value("\${koki.ai.vision.rest.connect-timeout-millis}") val visionConnectTimeoutMillis: Long,
) {
    companion object {
        const val CHAT_LLM_BEAN_NAME = "chat-llm"
        const val CHAT_TOOLS_LLM_BEAN_NAME = "chat-tools-llm"
        const val VISION_LLM_BEAN_NAME = "vision-llm"
    }

    @Bean(CHAT_LLM_BEAN_NAME)
    fun chatLLM(): LLM {
        return createLLM(chatType, chatModel, chatApiLey, chatReadTimeoutMillis, chatConnectTimeoutMillis)
    }

    @Bean(CHAT_TOOLS_LLM_BEAN_NAME)
    fun chatWithToolsLLM(): LLM {
        return createLLM(
            chatWithToolsType,
            chatWithToolsModel,
            chatWithToolsApiLey,
            chatWithToolsReadTimeoutMillis,
            chatWithToolsConnectTimeoutMillis
        )
    }

    @Bean(VISION_LLM_BEAN_NAME)
    fun visionLLM(): LLM {
        return createLLM(visionType, visionModel, visionApiLey, visionReadTimeoutMillis, visionConnectTimeoutMillis)
    }

    @Bean
    fun chatHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(chatLLM())
    }

    @Bean
    fun chatWithToolsHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(chatWithToolsLLM())
    }

    @Bean
    fun visionHealthCheck(): HealthIndicator {
        return LLMHealthIndicator(visionLLM())
    }

    private fun createLLM(
        type: String,
        model: String,
        apiKey: String,
        readTimeoutMillis: Long,
        connectTimeoutMillis: Long,
    ): LLM {
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
