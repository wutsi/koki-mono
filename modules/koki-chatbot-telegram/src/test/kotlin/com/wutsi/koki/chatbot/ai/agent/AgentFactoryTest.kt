package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.llm.LLM
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentFactoryTest {
    private val llm = mock<LLM>()
    private val searchRoomTool = mock<SearchRoomTool>()
    private val factory = AgentFactory(llm, searchRoomTool)

    @Test
    fun searchAgent() {
        val agent = factory.crateSearchAgent()
        assertEquals(llm, agent.llm)
        assertEquals(searchRoomTool, agent.searchRoomTool)
    }
}
