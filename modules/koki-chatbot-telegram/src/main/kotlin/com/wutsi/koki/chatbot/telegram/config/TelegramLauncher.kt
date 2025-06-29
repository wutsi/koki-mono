package com.wutsi.koki.chatbot.telegram.config

import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
class TelegramLauncher(
    private val telegramLongPollingApp: TelegramBotsLongPollingApplication,
    private val consumer: TelegramConsumer,

    @Value("\${koki.telegram.token}") private val token: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramLauncher::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info("Registering the bot")

        telegramLongPollingApp.registerBot(token, consumer)
    }
}
