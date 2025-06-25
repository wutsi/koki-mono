package com.wutsi.koki.chatbot.telegram.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
class TelegramConfiguration(
    private val llm: LLM,
    private val kokiRooms: KokiRooms,
    private val kokiRefData: KokiRefData,
    private val objectMapper: ObjectMapper,

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

    @Bean
    fun agentFactory(): AgentFactory {
        return AgentFactory(
            llm = llm,
            searchRoomTool = SearchRoomTool(
                kokiRooms = kokiRooms,
                kokiRefData = kokiRefData,
                objectMapper = objectMapper,
            ),
        )
    }
}
