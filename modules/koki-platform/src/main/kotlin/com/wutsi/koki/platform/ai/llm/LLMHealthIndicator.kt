package com.wutsi.koki.platform.ai.llm

import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class LLMHealthIndicator(
    private val llm: LLM
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LLMHealthIndicator::class.java)
    }

    override fun health(): Health {
        try {
            val now = System.currentTimeMillis()
            llm.generateContent(
                LLMRequest(
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
