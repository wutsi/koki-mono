package com.wutsi.koki.chatbot.telegram.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
class TelegramConfiguration(
    @Value("\${koki.telegram.token}") private val token: String,
) {
    @Bean
    fun telegramClient(): TelegramClient {
        return OkHttpTelegramClient(token)
    }
}
