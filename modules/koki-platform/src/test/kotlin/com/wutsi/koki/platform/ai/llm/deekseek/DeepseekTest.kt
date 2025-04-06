package com.wutsi.koki.platform.ai.llm.deekseek

import com.wutsi.koki.platform.ai.llm.Config
import com.wutsi.koki.platform.ai.llm.Document
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.ai.llm.Tool
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.platform.ai.llm.gemini.GeminiTest
import org.springframework.http.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepseekTest {
    private val llm = createLLM()

    @Test
    fun models() {
        val models = llm.models()

        assertEquals(
            listOf(
                "deepseek-chat"
            ),
            models
        )
    }

    @Test
    fun generateText() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        role = Role.USER,
                        text = "What is an API"
                    )
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> print(message) }
    }

    @Test
    fun generateJson() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        role = Role.SYSTEM,
                        text = "Your are a senior developer"
                    ),
                    Message(
                        role = Role.USER,
                        text = "Can you share an example of json of the request from Gemini API. Only the JSON please"
                    ),
                    Message(
                        role = Role.MODEL,
                        text = "Empty json:\n```json{}```"
                    ),
                ),
                config = Config(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        println("${response.messages.size} message(s)")
        assertEquals(true, response.messages[0].text?.startsWith("{"))
        assertEquals(true, response.messages[0].text?.endsWith("}"))

        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun processPDF() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = "Can you summarize in 500 word the following text:",
                        document = Document(
                            contentType = MediaType.APPLICATION_PDF,
                            content = GeminiTest::class.java.getResourceAsStream("/file/document-en.pdf")!!
                        )
                    )
                )
            )
        )
        print(response)
    }

    @Test
    fun functionCall() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = """
                            What's the current weather like in Montreal?
                        """.trimIndent(),
                    ),
                ),
                tools = listOf(
                    Tool(
                        functionDeclarations = listOf(
                            FunctionDeclaration(
                                name = "get_weather",
                                description = "Get the current real-time weather conditions for a specified city.",
                                parameters = FunctionParameters(
                                    properties = mapOf(
                                        "region" to FunctionParameterProperty(
                                            type = Type.STRING,
                                            description = "Region from where we want the weather"
                                        ),
                                        "unit" to FunctionParameterProperty(
                                            type = Type.STRING,
                                            description = "Optional. he temperature unit (Celsius or Fahrenheit). Defaults to Celsius if not specified.",
                                            enum = listOf("CELSIUS", "FAHRENHEIT")
                                        ),
                                    ),
                                    required = listOf("region")
                                )
                            )
                        )
                    )
                )
            )
        )
        print(response)
        assertEquals(Role.MODEL, response.messages[0].role)
        assertEquals(null, response.messages[0].document)
        assertEquals(null, response.messages[0].text)
        assertEquals("get_weather", response.messages[0].functionCall?.name)
    }

    private fun createLLM(): LLM {
        return Deepseek(
            apiKey = System.getenv("DEEPSEEK_API_KEY"),
            model = "deepseek-chat",
        )
    }

    private fun print(response: LLMResponse) {
        println("---------")
        println("Usage: ${response.usage}")
        println("${response.messages.size} message(s)")
        println()
        response.messages.forEach { message ->
            message.functionCall?.let { println("FUNCTION: ${message.functionCall}") }
            message.text?.let { println("TEXT: ${message.text}") }
        }
    }
}
