package com.wutsi.koki.platform.ai.llm.gemini

class GeminiTest { // : AbstractLLMTest()
    var i = 0
    val models = listOf(
        "gemini-2.5-pro",
        "gemini-2.5-flash-lite",
        "gemini-2.5-flash",
    )
    /*
        override fun createLLM(): LLM {
            return Gemini(
                apiKey = System.getenv("GEMINI_API_KEY"),
                model = models[i++ % models.size],
            )
        }

        override fun createVisionLLM(): LLM {
            return createLLM()
        }

     */
}
