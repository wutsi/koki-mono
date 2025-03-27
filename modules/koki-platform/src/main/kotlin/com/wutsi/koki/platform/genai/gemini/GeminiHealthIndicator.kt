package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.Message
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class GeminiHealthIndicator(
    private val gemini: Gemini
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GeminiHealthIndicator::class.java)
    }

    override fun health(): Health {
        try {
            val now = System.currentTimeMillis()
            gemini.generateContent(
                GenAIRequest(
                    messages = listOf(
                        Message(text = "Say hi!")
                    )
                )
            )
            return Health.up()
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .build()
        } catch (ex: Exception) {
            LOGGER.warn("Healthcheck error", ex)
            return Health.down()
                .withException(ex)
                .build()
        }
    }
}
