package com.wutsi.koki.platform.ai.genai.gemini

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.genai.Document
import com.wutsi.koki.platform.ai.genai.GenAIException
import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIResponse
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.Message
import com.wutsi.koki.platform.ai.genai.Role
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerateContentResponse
import com.wutsi.koki.platform.genai.FunctionDeclaration
import com.wutsi.koki.platform.genai.FunctionParameterProperty
import com.wutsi.koki.platform.genai.FunctionParameters
import com.wutsi.koki.platform.genai.Tool
import com.wutsi.koki.platform.genai.Type
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiTest {
    private val gemini = createGemini(RestTemplate())

    @Test
    fun generateText() {
        val response = gemini.generateContent(
            request = GenAIRequest(
                messages = listOf(
                    Message(
                        text = "What is an API"
                    )
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }

    @Test
    fun generateJson() {
        val response = gemini.generateContent(
            request = GenAIRequest(
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
        val response = gemini.generateContent(
            request = GenAIRequest(
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
        val response = gemini.generateContent(
            request = GenAIRequest(
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
        val response = gemini.generateContent(
            request = GenAIRequest(
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
        val response = gemini.generateContent(
            request = GenAIRequest(
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
        val response = gemini.generateContent(
            request = GenAIRequest(
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

    @Test
    fun httpError() {
        val ex = mock<HttpStatusCodeException>()
        doReturn(HttpStatusCode.valueOf(400)).whenever(ex).statusCode
        doReturn("Failed").whenever(ex).message

        val rest = mock<RestTemplate>()
        doThrow(ex).whenever(rest).postForEntity(any<String>(), any(), eq(GGenerateContentResponse::class.java))

        val result = assertThrows<GenAIException> {
            createGemini(rest).generateContent(
                request = GenAIRequest(
                    messages = listOf(
                        Message(
                            text = "What is an API"
                        )
                    )
                )
            )
        }

        assertEquals(400, result.statusCode)
        assertEquals("Failed", result.message)
        assertEquals(ex, result.cause)
    }

    @Test
    fun error() {
        val ex = IllegalStateException("Failed")

        val rest = mock<RestTemplate>()
        doThrow(ex).whenever(rest).postForEntity(any<String>(), any(), eq(GGenerateContentResponse::class.java))

        val result = assertThrows<GenAIException> {
            createGemini(rest).generateContent(
                request = GenAIRequest(
                    messages = listOf(
                        Message(
                            text = "What is an API"
                        )
                    )
                )
            )
        }

        assertEquals(-1, result.statusCode)
        assertEquals("Failed", result.message)
        assertEquals(ex, result.cause)
    }

    private fun createGemini(rest: RestTemplate): GenAIService {
        return Gemini(
            apiKey = System.getenv("GEMINI_API_KEY"),
            model = "gemini-2.0-flash",
            rest = rest
        )
    }

    private fun print(response: GenAIResponse) {
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
