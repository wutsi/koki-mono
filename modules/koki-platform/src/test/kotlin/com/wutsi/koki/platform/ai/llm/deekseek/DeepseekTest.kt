package com.wutsi.koki.platform.ai.llm.deekseek

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek

class DeepseekTest : AbstractLLMTest() {
    override fun image() {
        // Nothing - deepseek does not support image generation
    }

    override fun systemInstructions() {
        // Ignore
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
