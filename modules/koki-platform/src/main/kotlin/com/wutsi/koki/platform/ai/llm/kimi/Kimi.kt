package com.wutsi.koki.platform.ai.llm.kimi

import com.wutsi.koki.platform.ai.llm.LLMFunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLMFunctionParameters
import com.wutsi.koki.platform.ai.llm.Tool
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek

class Kimi(
    apiKey: String,
    model: String,
    readTimeoutMillis: Long = 60000,
    connectTimeoutMillis: Long = 30000,
) : Deepseek(apiKey, model, readTimeoutMillis, connectTimeoutMillis) {
    override fun getEndpoint(): String {
        return "https://api.moonshot.ai/v1/chat/completions"
    }

    override fun getFunctionType(function: LLMFunctionDeclaration): String {
        return if (function.builtIn) "builtin_function" else "function"
    }

    override fun getBuiltInTools(): List<Tool> {
        return listOf(
            KimiTool(
                func = LLMFunctionDeclaration(
                    builtIn = true,
                    name = "\$web_search",
                    description = "Built-in tool to search the web for relevant information.",
                    parameters = LLMFunctionParameters(),
                )
            )
        )
    }
}
