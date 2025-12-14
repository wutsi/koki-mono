package com.wutsi.koki.platform.ai.llm

import org.junit.jupiter.api.AfterEach
import org.springframework.http.MediaType
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbstractLLMTest {
    protected abstract fun createLLM(): LLM
    protected abstract fun createVisionLLM(): LLM

    protected open fun delayMillis(): Long {
        return 30000L
    }

    @AfterEach
    fun tearDown() {
        Thread.sleep(delayMillis())
    }

    @Test
    open fun chat() {
        var response: LLMResponse = LLMResponse()
        val time = measureTimeMillis {
            response = createLLM().generateContent(
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
    open fun json() {
        val response = createLLM().generateContent(
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
    open fun systemInstructions() {
        val response = createLLM().generateContent(
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
    open fun pdf() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = "Can you summarize in 500 word the content of this document",
                        document = Document(
                            contentType = MediaType.APPLICATION_PDF,
                            content = AbstractLLMTest::class.java.getResourceAsStream("/file/document-en.pdf")!!
                        )
                    )
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    open fun image() {
        val response = createVisionLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        text = """
                            Can you extract the information of this image.
                        """.trimIndent(),
                        document = Document(
                            contentType = MediaType.IMAGE_JPEG,
                            content = AbstractLLMTest::class.java.getResourceAsStream("/file/document.jpg")!!
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
    open fun function() {
        val response = createLLM().generateContent(
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
    }

    protected fun print(response: LLMResponse) {
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
