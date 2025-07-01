package com.wutsi.koki.chatbot.messenger.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatbotConfiguration(
    private val llm: LLM,
    private val kokiRooms: KokiRooms,
    private val kokiRefData: KokiRefData,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun agentFactory(): AgentFactory {
        return AgentFactory(
            llm = llm,
        )
    }

    @Bean
    fun chatbot(): Chatbot {
        return Chatbot(
            kokiRooms = kokiRooms,
            kokiRefData = kokiRefData,
            objectMapper = objectMapper,
            agentFactory = agentFactory(),
            maxRecommendation = 3,
        )
    }
}
