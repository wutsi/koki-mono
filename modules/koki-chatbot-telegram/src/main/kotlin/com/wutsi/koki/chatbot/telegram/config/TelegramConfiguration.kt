package com.wutsi.koki.chatbot.telegram.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
class TelegramConfiguration(
    @Value("\${koki.telegram.token}") private val token: String,
) {
    @Bean(destroyMethod = "close")
    fun telegramLongPollingApp(): TelegramBotsLongPollingApplication {
        return TelegramBotsLongPollingApplication()
    }

    @Bean
    fun telegramClient(): TelegramClient {
        return OkHttpTelegramClient(token)
    }
}
