package com.wutsi.koki.chatbot.telegram.config

import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
class TelegramLauncher(
    private val telegramLongPollingApp: TelegramBotsLongPollingApplication,
    private val consumer: TelegramConsumer,

    @Value("\${koki.telegram.token}") private val token: String,
) {
    @PostConstruct
    fun init() {
        telegramLongPollingApp.registerBot(token, consumer)
    }
}
