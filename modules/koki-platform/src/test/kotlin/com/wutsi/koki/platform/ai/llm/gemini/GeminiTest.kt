package com.wutsi.koki.platform.ai.llm.gemini

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
import org.junit.jupiter.api.AfterEach
import org.springframework.http.MediaType
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiTest {
    private val llm = createGemini()

    @Test
    fun models() {
        val models = llm.models()
        assertEquals(
            listOf(
                "gemini-2.5-flash",
                "gemini-2.0-flash",
                "gemini-2.0-flash-lite",
                "gemini-1.5-pro",
                "gemini-1.5-flash",
                "gemini-1.5-flash-8b"
            ),
            models,
        )
    }

    @AfterEach
    fun tearDown() {
        Thread.sleep(15000)
    }

    @Test
    fun generateText() {
        var response: LLMResponse = LLMResponse()
        val time = measureTimeMillis {
            response = llm.generateContent(
                request = LLMRequest(
                    messages = listOf(
                        Message(
                            text = "What is an API"
                        )
                    )
                )
            )
        }
        println("${response.messages.size} message(s). duration=$time ms")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun gemeni25() {
        var response: LLMResponse = LLMResponse()
        val time = measureTimeMillis {
            response = createGemini("gemini-2.5-flash").generateContent(
                request = LLMRequest(
                    messages = listOf(
                        Message(
                            text = "What is an API"
                        )
                    )
                )
            )
        }
        println("${response.messages.size} message(s). duration=$time ms")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun generateJson() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = "Can you share an example of json of the request from Gemini API. Only the JSON please"
                    )
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
    fun generateWithConfig() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = "What is an API"
                    )
                ),
                config = Config(
                    temperature = .9,
                    maxOutputTokens = 100
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun generateWithSystemInstructions() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        role = Role.MODEL,
                        text = """
                            You are a senior software developer, your name is Joe.
                            You should just produce code, nothing else.
                        """.trimIndent()
                    ),
                    Message(
                        role = Role.USER,
                        text = """
                            Can you write code to count the number of words in a text in Kotlin.
                            Add also unit tests en ensure that the code works as expected.
                        """.trimIndent()
                    )
                ),
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun processPDF() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = "Can you summarize in 500 word the content of this document",
                        document = Document(
                            contentType = MediaType.APPLICATION_PDF,
                            content = GeminiTest::class.java.getResourceAsStream("/file/document-en.pdf")!!
                        )
                    )
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun processImage() {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = """
                            Can you extract the information of this image.
                        """.trimIndent(),
                        document = Document(
                            contentType = MediaType.IMAGE_JPEG,
                            content = GeminiTest::class.java.getResourceAsStream("/file/document.jpg")!!
                        )
                    ),
                ),
                config = Config(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
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
                config = Config(
                    responseType = MediaType.APPLICATION_JSON,
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
    }

    private fun createGemini(model: String = "gemini-2.0-flash"): LLM {
        return Gemini(
            apiKey = System.getenv("GEMINI_API_KEY"),
            model = model,
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
