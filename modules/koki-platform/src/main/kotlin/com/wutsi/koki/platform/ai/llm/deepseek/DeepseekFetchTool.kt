package com.wutsi.koki.platform.ai.llm.deepseek

import com.wutsi.koki.platform.ai.llm.LLMFunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLMFunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.LLMFunctionParameters
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.Tool

class DeepseekFetchTool(private val fetch: Fetch) : Tool {
    override fun function(): LLMFunctionDeclaration = LLMFunctionDeclaration(
        name = "deepseek_fetch",
        description = """
            URL content extraction tool. The content is converted to markdown
        """.trimIndent(),
        parameters = LLMFunctionParameters(
            type = LLMType.OBJECT,
            properties = mapOf(
                "url" to LLMFunctionParameterProperty(
                    type = LLMType.STRING,
                    description = "URL of the web page to fetch",
                ),
            ),
            required = listOf("url")
        ),
    )

    override fun use(args: Map<String, Any>): String {
        val url = args["url"] as String
        return "BEGIN CONTENT of: `$url`:\n" +
            fetch.fetch(args["url"] as String) + "\n" +
            "END CONTENT of $url\n\n"
    }
}
