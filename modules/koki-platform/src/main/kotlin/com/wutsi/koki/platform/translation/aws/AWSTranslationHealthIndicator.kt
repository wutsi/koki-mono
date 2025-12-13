package com.wutsi.koki.platform.translation.aws

import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.model.TranslateTextRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator

class AWSTranslationHealthIndicator(
    private val translator: AmazonTranslate
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AWSTranslationHealthIndicator::class.java)
    }

    override fun health(): Health {
        val start = System.currentTimeMillis()
        try {
            val request = TranslateTextRequest()
            request.text = "Hello world"
            request.sourceLanguageCode = "en"
            request.targetLanguageCode = "fr"

            translator.translateText(request)
            return Health.up().withDetail("latency", System.currentTimeMillis() - start).build()
        } catch (ex: Exception) {
            LOGGER.warn("Healthcheck error", ex)
            return Health.down().withDetail("latency", System.currentTimeMillis() - start).withException(ex).build()
        }
    }
}
