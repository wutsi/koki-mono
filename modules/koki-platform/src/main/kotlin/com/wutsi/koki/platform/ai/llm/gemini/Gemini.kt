package com.wutsi.koki.platform.ai.llm.gemini

import com.wutsi.koki.platform.ai.llm.Config
import com.wutsi.koki.platform.ai.llm.Content
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
import com.wutsi.koki.platform.ai.llm.gemini.model.GThinkingConfig
import org.apache.commons.io.IOUtils
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.HttpStatusCodeException
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Base64

class Gemini(
    private val apiKey: String,
    private val model: String,
    private val readTimeoutMillis: Long = 60000,
    private val connectTimeoutMillis: Long = 30000,
) : LLM {
    private val rest = RestTemplateBuilder()
        .readTimeout(Duration.of(readTimeoutMillis, ChronoUnit.MILLIS))
        .connectTimeout(Duration.of(connectTimeoutMillis, ChronoUnit.MILLIS))
        .additionalMessageConverters(
            JacksonJsonHttpMessageConverter(
                JsonMapper.builderWithJackson2Defaults()
                    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .build()
            )
        )
        .build()

    override fun generateContent(request: LLMRequest): LLMResponse {
        val req = GGenerateContentRequest(
            contents = request.messages.map { message ->
                GContent(
                    role = toRole(message.role),
                    parts = message.content.mapNotNull { item ->
                        if (item.text != null) {
                            GPart(text = item.text)
                        } else if (item.document != null) {
                            GPart(
                                inlineData = GInlineData(
                                    mimeType = item.document.contentType.toString(),
                                    data = Base64.getEncoder()
                                        .encodeToString(IOUtils.toByteArray(item.document.content))
                                )
                            )
                        } else {
                            null
                        }
                    }
                )
            },
            generationConfig = createConfig(request.config),
            tools = request.tools,
            toolConfig = request.toolConfig,
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
                    .map { part ->
                        Message(
                            role = Role.MODEL,
                            content = listOfNotNull(
                                encodeResponse(
                                    request.config?.responseType,
                                    part.text
                                )?.let { text -> Content(text = text) },
                                part.functionCall?.let { function ->
                                    Content(
                                        functionCall = FunctionCall(
                                            name = if (function.name.startsWith("default_api.")) {
                                                function.name.substring(12)
                                            } else {
                                                function.name
                                            },
                                            args = function.args,
                                        )
                                    )
                                }
                            ),
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

    private fun createConfig(config: Config?): GGenerationConfig? {
        if (config == null) {
            if (supportsThinking()) {
                return GGenerationConfig(
                    thinkingConfig = GThinkingConfig(
                        thinkingBudget = 0
                    )
                )
            }
        } else {
            return GGenerationConfig(
                temperature = config.temperature,
                topP = config.topP,
                topK = config.topK,
                maxOutputTokens = config.maxOutputTokens,
                thinkingConfig = if (supportsThinking()) {
                    GThinkingConfig(
                        thinkingBudget = 0
                    )
                } else {
                    null
                }
            )
        }
        return null
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

    private fun supportsThinking(): Boolean {
        val version = getModelVersion().toDouble()
        return version >= 2.5
    }

    private fun getModelVersion(): String {
        // gemini-2.5-flash
        val i = "gemini-".length
        val j = model.indexOf("-", i)
        return model.substring(i, j)
    }
}
