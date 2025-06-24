package com.wutsi.koki.chatbot.telegram.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.url.UrlShortener
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.concurrent.ExecutorService

@Configuration
class TelegramConfiguration(
    private val llm: LLM,
    private val kokiRooms: KokiRooms,
    private val kokiRefData: KokiRefData,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
    private val objectMapper: ObjectMapper,
    private val executorService: ExecutorService,
    private val urlShortener: UrlShortener,

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
    fun telegramConsumer(): TelegramConsumer {
        return TelegramConsumer(
            client = telegramClient(),
            tenantProvider = tenantProvider,
            tenantService = tenantService,
            objectMapper = objectMapper,
            executorService = executorService,
            agentFactory = agentFactory(),
            urlShortener = urlShortener,
        )
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
