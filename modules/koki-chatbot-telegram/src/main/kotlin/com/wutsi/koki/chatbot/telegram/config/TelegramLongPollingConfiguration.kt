package com.wutsi.koki.chatbot.telegram.config

import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
@ConditionalOnProperty(
    value = ["koki.telegram.consumer-type"],
    havingValue = "long-polling",
    matchIfMissing = true
)
class TelegramLongPollingConfiguration(
    private val consumer: TelegramConsumer,

    @Value("\${koki.telegram.token}") private val token: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramLongPollingConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Initializing")
        val app = TelegramBotsLongPollingApplication()
        app.registerBot(token, consumer)
    }
}
