package com.wutsi.koki.platform.ai.llm.deepseek

import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMDocumentTypeNotSupportedException
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
import org.apache.commons.io.IOUtils
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.HttpStatusCodeException
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.UUID

class Deepseek(
    private val apiKey: String,
    private val model: String,
    private val readTimeoutMillis: Long = 60000,
    private val connectTimeoutMillis: Long = 30000,
) : LLM {
    private val jsonMapper = JsonMapper.builderWithJackson2Defaults()
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
    private val rest = RestTemplateBuilder()
        .readTimeout(Duration.of(readTimeoutMillis, ChronoUnit.MILLIS))
        .connectTimeout(Duration.of(connectTimeoutMillis, ChronoUnit.MILLIS))
        .additionalMessageConverters(JacksonJsonHttpMessageConverter(jsonMapper))
        .build()

    override fun models(): List<String> {
        return listOf(
            "deepseek-chat"
        )
    }

    override fun generateContent(request: LLMRequest): LLMResponse {
        val req = DSCompletionRequest(
            model = model,
            messages = request.messages.flatMap { message -> toDSMessage(message) },
            temperature = request.config?.temperature,
            topP = request.config?.topP,
            tools = request.tools?.ifEmpty { null }?.let { tools ->
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
            val resp = rest.postForEntity(
                "https://api.deepseek.com/chat/completions",
                entity,
                DSCompletionResponse::class.java
            ).body!!

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
                                args = if (call.function.arguments.isEmpty()) {
                                    emptyMap()
                                } else {
                                    jsonMapper.readValue(
                                        call.function.arguments,
                                        Map::class.java
                                    ).map { entry -> entry.key.toString() to entry.value.toString() }
                                        .toMap()
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

    private fun toDSMessage(message: Message): List<DSMessage> {
        val role = when (message.role) {
            Role.USER -> "user"
            Role.SYSTEM, Role.MODEL -> "system"
        }

        val result = mutableListOf<DSMessage>()

        if (message.text != null) {
            result.add(
                DSMessage(
                    content = message.text,
                    role = role
                )
            )
        }
        if (message.document != null) {
            val contentType = message.document.contentType
            if (contentType.toString().startsWith("text/") || contentType == MediaType.APPLICATION_JSON) {
                result.add(
                    DSMessage(
                        content = IOUtils.toString(message.document.content, "utf-8"),
                        role = role
                    )
                )
            } else if (contentType == MediaType.APPLICATION_PDF) {
                result.add(
                    DSMessage(
                        content = pdf2Text(message.document.content),
                        role = role
                    )
                )
            } else {
                throw LLMDocumentTypeNotSupportedException(contentType)
            }
        }
        return result
    }

    private fun pdf2Text(input: InputStream): String {
        val file = File.createTempFile(UUID.randomUUID().toString(), ".pdf")
        try {
            // Copy to file
            val output = FileOutputStream(file)
            output.use {
                IOUtils.copy(input, output)
            }

            // Extract the text
            val doc = Loader.loadPDF(file)
            val stripper = PDFTextStripper()
            stripper.startPage = 1
            stripper.endPage = doc.numberOfPages
            return stripper.getText(doc)
        } finally {
            file.delete()
        }
    }
}
