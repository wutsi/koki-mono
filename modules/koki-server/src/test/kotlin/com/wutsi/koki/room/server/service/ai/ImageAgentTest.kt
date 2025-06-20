package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.platform.ai.llm.LLM
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageAgentTest {
    private val llm = mock<LLM>()
    private val agent = ImageAgent(llm)

    @Test
    fun systemInstructions() {
        assertEquals(
            ImageAgent.SYSTEM_INSTRUCTIONS.trimIndent(),
            agent.systemInstructions()
        )
    }

    @Test
    fun `prompt contains query`() {
        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        assertEquals(true, prompt.contains("Query: This is my query"))
    }

    @Test
    fun `prompt contains observations`() {
        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        assertEquals(true, prompt.contains("Observations:\n- A\n- B"))
    }
}
