package com.wutsi.koki.chatbot.telegram.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
@ConditionalOnProperty(
    value = ["koki.telegram.consumer-type"],
    havingValue = "webhook",
    matchIfMissing = true
)
class TelegramWebhookConfiguration(
    private val telegram: TelegramClient,

    @Value("\${koki.webapp.base-url}") private val baseUrl: Boolean,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramWebhookConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        val webhookUrl = "$baseUrl/webhook"

        LOGGER.info(">>> Initializing the webhook: $webhookUrl")
        val result = telegram.execute(SetWebhook(webhookUrl))
        LOGGER.info(">>> Webhook initialization. success=$result")
    }
}
