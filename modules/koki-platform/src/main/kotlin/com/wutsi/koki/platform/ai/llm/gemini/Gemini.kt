package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMException
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.ai.llm.Usage
import com.wutsi.koki.platform.ai.llm.gemini.model.GContent
import com.wutsi.koki.platform.ai.llm.gemini.model.GGenerateContentRequest
import com.wutsi.koki.platform.ai.llm.gemini.model.GGenerateContentResponse
import com.wutsi.koki.platform.ai.llm.gemini.model.GGenerationConfig
import com.wutsi.koki.platform.ai.llm.gemini.model.GInlineData
import com.wutsi.koki.platform.ai.llm.gemini.model.GPart
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.util.Base64

class Gemini(
    private val apiKey: String,
    private val model: String,
    private val rest: RestTemplate,
) : LLM {
    override fun generateContent(request: LLMRequest): LLMResponse {
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
            return LLMResponse(
                messages = resp.candidates
                    .flatMap { candidate -> candidate.content.parts }
                    .mapNotNull { part ->
                        Message(
                            role = Role.MODEL,
                            text = encodeResponse(request.config?.responseType, part.text),
                            functionCall = part.functionCall?.let { function ->
                                FunctionCall(
                                    name = if (function.name.startsWith("default_api.")) {
                                        function.name.substring(12)
                                    } else {
                                        function.name
                                    },
                                    args = function.args,
                                )
                            }
                        )
                    }.filter { message -> message.text != null || message.functionCall != null },

                usage = resp.usageMetadata?.let { usage ->
                    Usage(
                        totalTokenCount = usage.totalTokenCount,
                        promptTokenCount = usage.promptTokenCount,
                        responseTokenCount = usage.promptTokenCount,
                    )
                }
            )
        } catch (ex: HttpStatusCodeException) {
            throw LLMException(
                statusCode = ex.statusCode.value(),
                message = ex.message,
                cause = ex,
            )
        } catch (ex: Exception) {
            throw LLMException(
                statusCode = -1,
                message = ex.message,
                cause = ex,
            )
        }
    }

    private fun toRole(role: Role): String {
        return when (role) {
            Role.USER -> "user"
            Role.MODEL, Role.SYSTEM -> "model"
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
