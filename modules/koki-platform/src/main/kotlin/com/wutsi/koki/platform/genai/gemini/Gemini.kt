package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.GenAIException
import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIResponse
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.Message
import com.wutsi.koki.platform.ai.genai.Role
import com.wutsi.koki.platform.ai.genai.gemini.model.GContent
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerateContentRequest
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerateContentResponse
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerationConfig
import com.wutsi.koki.platform.ai.genai.gemini.model.GInlineData
import com.wutsi.koki.platform.ai.genai.gemini.model.GPart
import com.wutsi.koki.platform.genai.FunctionCall
import com.wutsi.koki.platform.genai.Usage
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.util.Base64

class Gemini(
    private val apiKey: String,
    private val model: String,
    private val rest: RestTemplate,
) : GenAIService {
    override fun generateContent(request: GenAIRequest): GenAIResponse {
        val req = GGenerateContentRequest(
            contents = request.messages.map { message ->
                GContent(
                    role = toRole(message.role),
                    parts = listOf(
                        message.text?.let { text -> GPart(text = text) },
                        message.document?.let { document ->
                            GPart(
                                inlineData = GInlineData(
                                    mimeType = document.contentType.toString(),
                                    data = Base64.getEncoder()
                                        .encodeToString(IOUtils.toByteArray(document.content))
                                )
                            )
                        }
                    ).filterNotNull()
                )
            },
            generationConfig = request.config?.let { config ->
                GGenerationConfig(
                    temperature = config.temperature,
                    topP = config.topP,
                    topK = config.topK,
                    maxOutputTokens = config.maxOutputTokens,
                )
            },
            tools = request.tools,
        )
        try {
            val resp = rest.postForEntity(
                "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey",
                req,
                GGenerateContentResponse::class.java
            ).body!!
            return GenAIResponse(
                messages = resp.candidates
                    .flatMap { candidate -> candidate.content.parts }
                    .map { part ->
                        Message(
                            text = encodeResponse(request.config?.responseType, part.text),
                            functionCall = part.functionCall?.let { function ->
                                FunctionCall(
                                    name = function.name,
                                    args = function.args,
                                )
                            }
                        )
                    },

                usage = resp.usageMetadata?.let { usage ->
                    Usage(
                        totalTokenCount = usage.totalTokenCount,
                        promptTokenCount = usage.promptTokenCount,
                        responseTokenCount = usage.promptTokenCount,
                    )
                }
            )
        } catch (ex: HttpStatusCodeException) {
            throw GenAIException(
                statusCode = ex.statusCode.value(),
                message = ex.message,
                cause = ex,
            )
        } catch (ex: Exception) {
            throw GenAIException(
                statusCode = -1,
                message = ex.message,
                cause = ex,
            )
        }
    }

    private fun toRole(role: Role): String {
        return when (role) {
            Role.USER -> "user"
            Role.MODEL -> "model"
        }
    }

    private fun encodeResponse(responseType: MediaType?, text: String?): String? {
        if (text == null) {
            return null
        } else if (responseType == MediaType.APPLICATION_JSON) {
            return extractJsonFragment(text)
        } else {
            return text
        }
    }

    private fun extractJsonFragment(text: String): String? {
        val startIndex = text.indexOf("{")
        val endIndex = text.lastIndexOf("}")

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return null // No valid JSON found
        }

        return text.substring(startIndex, endIndex + 1)
    }
}
