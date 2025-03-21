package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.Document
import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.Message
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.io.InputStream
import java.net.URL
import kotlin.test.Test

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
            )
        )
        println("${response.messages.size} message(s)")
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
    fun processPDF() {
        val response = gemini.generateContent(
            request = GenAIRequest(
                messages = listOf(
                    Message(
                        text = "Can you summarize in 500 word the content of this document",
                        document = Document(
                            contentType = MediaType.APPLICATION_PDF,
                            content = downloadDocument("https://discovery.ucl.ac.uk/id/eprint/10089234/1/343019_3_art_0_py4t4l_convrt.pdf")
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
                            The response must be in JSON.
                        """.trimIndent(),
                        document = Document(
                            contentType = MediaType.IMAGE_JPEG,
                            content = downloadDocument("https://as1.ftcdn.net/v2/jpg/04/06/03/36/1000_F_406033645_AcLheutjL6TN3LcR0kIEeL5XyJCLcgEc.jpg")
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

    private fun downloadDocument(url: String): ByteArray {
        println("Downloading...")
        val inputStream: InputStream = URL(url).openStream()
        inputStream.use {
            val result = inputStream.readAllBytes()

            println("Downloaded...")
            return result
        }
    }
}
