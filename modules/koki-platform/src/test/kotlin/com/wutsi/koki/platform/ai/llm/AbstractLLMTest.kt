package com.wutsi.koki.platform.ai.llm

import org.junit.jupiter.api.AfterEach
import org.springframework.http.MediaType
import kotlin.test.Test

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
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        content = listOf(Content(text = "What is an API")),
                    )
                )
            )
        )
        print(response)
    }

    @Test
    open fun json() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                text = "Can you share an example of json of the request from Gemini API. Only the JSON please"
                            )
                        ),
                    )
                ),
                config = Config(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        print(response)
//        JsonMapper().readValue(response.messages[0].text, Map::class.java)
    }

    @Test
    open fun systemInstructions() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        role = Role.MODEL,
                        content = listOf(
                            Content(
                                text = """
                                    You are a senior software developer, your name is Joe.
                                    You should just produce code, nothing else.
                                """.trimIndent()
                            )
                        ),
                    ),
                    Message(
                        role = Role.USER,
                        content = listOf(
                            Content(
                                text = """
                                    Can you write code to count the number of words in a text in Kotlin.
                                    Add also unit tests en ensure that the code works as expected.
                                """.trimIndent()
                            )
                        )
                    )
                ),
                config = Config(
                    temperature = .9,
                    maxOutputTokens = 100
                )
            )
        )
        print(response)
    }

    @Test
    open fun pdf() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                text = "Can you summarize in 500 word the content of this document",
                            ),
                            Content(
                                document = Document(
                                    contentType = MediaType.APPLICATION_PDF,
                                    content = AbstractLLMTest::class.java.getResourceAsStream("/file/document-en.pdf")!!
                                )
                            )
                        )
                    )
                )
            )
        )
        print(response)
    }

    @Test
    open fun image() {
        val response = createVisionLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                text = """
                                    Can you extract the information of this image in JSON format having the following field:
                                    - Name
                                    - Document Type
                                    - Document ID
                                    - Expiry Date
                                """.trimIndent()
                            ),
                            Content(
                                document = Document(
                                    contentType = MediaType.IMAGE_JPEG,
                                    content = AbstractLLMTest::class.java.getResourceAsStream("/file/document.jpg")!!
                                )
                            )
                        )
                    ),
                ),
                config = Config(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        print(response)
    }

    @Test
    open fun function() {
        val response = createLLM().generateContent(
            request = LLMRequest(
                messages = listOf(
                    Message(
                        content = listOf(
                            Content(
                                text = "What's the current weather like in Montreal?"
                            )
                        )
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
            message.content.forEach { content ->
                content.text?.let { println("TEXT: ${content.text}") }
                content.functionCall?.let { println("FUNCTION: ${content.functionCall}") }
            }
        }
    }
}
