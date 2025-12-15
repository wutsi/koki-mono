package com.wutsi.koki.platform.ai.llm.deekseek

import com.wutsi.koki.platform.ai.llm.AbstractLLMTest
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
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
