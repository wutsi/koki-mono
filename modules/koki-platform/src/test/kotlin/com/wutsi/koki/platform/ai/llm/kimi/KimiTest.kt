package com.wutsi.koki.platform.ai.llm.kimi

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMContent
import com.wutsi.koki.platform.ai.llm.LLMMessage
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMTool
import org.junit.jupiter.api.Test

class KimiTest : AbstractLLMTest() {
    override fun createVisionLLM(): LLM {
        return Kimi(
            apiKey = System.getenv("KIMI_API_KEY"),
            model = "moonshot-v1-128k-vision-preview"
        )
    }

    override fun createLLM(): LLM {
        return Kimi(
            apiKey = System.getenv("KIMI_API_KEY"),
            model = "kimi-k2-turbo-preview",
        )
    }

    @Test
    override fun image() {
        super.image()
    }

    @Test
    fun buildInTools() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    LLMMessage(
                        content = listOf(
                            LLMContent(
                                text = "What's the current weather like in Montreal?"
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
        print(response)
    }
}
