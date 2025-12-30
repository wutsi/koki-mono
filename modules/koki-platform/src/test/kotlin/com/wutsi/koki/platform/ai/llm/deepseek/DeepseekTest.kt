package com.wutsi.koki.platform.ai.llm.deepseek

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMContent
import com.wutsi.koki.platform.ai.llm.LLMMessage
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMTool
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Ignore
import kotlin.test.Test

class DeepseekTest : AbstractLLMTest() {
    @Test
    @Ignore
    override fun image() {
        // Nothing - deepseek does not support image generation
    }

    @Test
    @Ignore("doesn't work with deepseek")
    override fun systemInstructions() {
        super.systemInstructions()
    }

    @Test
    override fun function() {
        super.function()
    }

    @Test
    fun buildInTools() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    LLMMessage(
                        content = listOf(
                            LLMContent(
                                text = "Can you search on internet the current temperature in Montreal?"
                            )
                        )
                    ),
                ),
                tools = createLLM().getBuiltInTools().map { tool ->
                    LLMTool(
                        functionDeclarations = listOf(tool.function())
                    )
                }
            )
        )

        val fc = response.messages.firstOrNull()?.content?.find { content -> content.functionCall != null }
        assertEquals("deepseek_websearch", fc?.functionCall?.name)
    }

    override fun createLLM(): LLM {
        return Deepseek(
            apiKey = System.getenv("DEEPSEEK_API_KEY"),
            model = "deepseek-chat",
        )
    }

    override fun createVisionLLM(): LLM {
        return createLLM()
    }
}
