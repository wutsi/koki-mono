package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.platform.ai.llm.LLM
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentFactoryTest {
    private val llm = mock<LLM>()
    private val factory = AgentFactory(llm)

    @Test
    fun searchParameterAgent() {
        val agent = factory.createSearchParameterAgent()
        assertEquals(llm, agent.llm)
    }
}
