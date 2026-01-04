package com.wutsi.koki.platform.ai.llm.deepseek

import com.wutsi.koki.platform.ai.llm.LLMFunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLMFunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.LLMFunctionParameters
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.Tool

class DeepseekWebsearchTool(private val websearch: Websearch) : Tool {
    override fun function(): LLMFunctionDeclaration = LLMFunctionDeclaration(
        name = "deepseek_websearch",
        description = """
            Performs real-time information and internet searches.
        """.trimIndent(),
        parameters = LLMFunctionParameters(
            type = LLMType.OBJECT,
            properties = mapOf(
                "q" to LLMFunctionParameterProperty(
                    type = LLMType.STRING,
                    description = "The search query",
                ),
            ),
            required = listOf("q")
        ),
    )

    override fun use(args: Map<String, Any>): String {
        val query = args["q"] as String
        return "Results for the web search of: `$query`\n" +
            websearch.search(query) + "\n\n"
    }
}
