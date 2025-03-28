package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.Document
import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.Message
import com.wutsi.koki.platform.ai.genai.Role
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiTest {
    private val gemini: GenAIService = Gemini(
        apiKey = System.getenv("GEMINI_API_KEY"),
        model = "gemini-2.0-flash",
        rest = RestTemplate()
    )

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
                config = GenAIConfig(
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
                config = GenAIConfig(
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
                config = GenAIConfig(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        println("${response.messages.size} message(s)")
        response.messages.forEach { message -> println(message.text) }
    }
}
