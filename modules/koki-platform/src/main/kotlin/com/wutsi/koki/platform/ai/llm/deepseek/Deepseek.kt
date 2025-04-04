package com.wutsi.koki.platform.ai.llm.deepseek

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMException
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.ai.llm.Usage
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSCompletionRequest
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSCompletionResponse
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSMessage
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSTool
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.collections.flatMap

class Deepseek(
    private val apiKey: String,
    private val model: String,
    private val rest: RestTemplate,
    private val objectMapper: ObjectMapper,
) : LLM {
    override fun generateContent(request: LLMRequest): LLMResponse {
        val req = DSCompletionRequest(
            model = model,
            messages = request.messages.mapNotNull { message ->
                message.text?.let { text ->
                    DSMessage(
                        content = text,
                        role = when (message.role) {
                            Role.USER -> "user"
                            Role.SYSTEM, Role.MODEL -> "system"
                        }
                    )
                }
            },
            temperature = request.config?.temperature,
            topP = request.config?.topP,
            tools = request.tools?.let { tools ->
                tools.flatMap { tool -> tool.functionDeclarations }
                    .map { function ->
                        DSTool(
                            type = "function",
                            function = DSFunction(
                                name = function.name,
                                description = function.description,
                                parameters = function.parameters?.let { params ->
                                    DSFunctionParameters(
                                        type = params.type.name.lowercase(),
                                        required = params.required,
                                        properties = params.properties.map { entry ->
                                            entry.key to DSFunctionParameterProperty(
                                                description = entry.value.description,
                                                type = entry.value.type.name.lowercase(),
                                                enum = entry.value.enum,
                                            )
                                        }.toMap()
                                    )
                                }
                            )
                        )
                    }
            },
            responseFormat = if (request.config?.responseType == MediaType.APPLICATION_JSON) {
                mapOf("type" to "json_object")
            } else {
                null
            }
        )

        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.set("Authorization", "Bearer $apiKey")

        try {
            val entity = HttpEntity<DSCompletionRequest>(req, headers)
            val resp = rest.postForObject(
                "https://api.deepseek.com/chat/completions",
                entity,
                DSCompletionResponse::class.java
            )

            return LLMResponse(
                messages = resp.choices.map { choice ->
                    Message(
                        role = when (choice.message.role) {
                            "system" -> Role.SYSTEM
                            "assistant", "model" -> Role.MODEL
                            else -> Role.USER
                        },
                        text = choice.message.content?.ifEmpty { null },
                        functionCall = choice.message.toolCalls.firstOrNull()?.let { call ->
                            FunctionCall(
                                name = call.function.name,
                                args = if (call.function.arguments.isNullOrEmpty()) {
                                    emptyMap()
                                } else {
                                    objectMapper.readValue(
                                        call.function.arguments,
                                        Any::class.java
                                    ) as Map<String, String>
                                }
                            )
                        }
                    )
                },
                usage = resp.usage?.let { usage ->
                    Usage(
                        promptTokenCount = usage.promptTokens,
                        responseTokenCount = usage.completionTokens,
                        totalTokenCount = usage.totalTokens,
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
}
