package com.wutsi.koki.platform.ai.llm.deepseek

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMContent
import com.wutsi.koki.platform.ai.llm.LLMDocumentTypeNotSupportedException
import com.wutsi.koki.platform.ai.llm.LLMException
import com.wutsi.koki.platform.ai.llm.LLMFunctionCall
import com.wutsi.koki.platform.ai.llm.LLMFunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLMMessage
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.LLMRole
import com.wutsi.koki.platform.ai.llm.LLMUsage
import com.wutsi.koki.platform.ai.llm.Tool
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSCompletionRequest
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSCompletionResponse
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSContent
import com.wutsi.koki.platform.ai.llm.deepseek.model.DSImageUrl
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
import java.util.Base64
import java.util.UUID

/**
 * Deepseek LLM implementation that used OpenAPI API (for deepseek)
 */
open class Deepseek(
    private val apiKey: String,
    private val model: String,
    private val readTimeoutMillis: Long = 60000,
    private val connectTimeoutMillis: Long = 30000,
) : LLM {
    companion object {
        const val DEEPSEEK_ENDPOINT = "https://api.deepseek.com/chat/completions"
    }

    private val jsonMapper = JsonMapper.builderWithJackson2Defaults()
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
    private val rest = RestTemplateBuilder()
        .readTimeout(Duration.of(readTimeoutMillis, ChronoUnit.MILLIS))
        .connectTimeout(Duration.of(connectTimeoutMillis, ChronoUnit.MILLIS))
        .additionalMessageConverters(JacksonJsonHttpMessageConverter(jsonMapper))
        .build()

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
                            type = getFunctionType(function),
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
                getEndpoint(),
                entity,
                DSCompletionResponse::class.java
            ).body!!

            return LLMResponse(
                messages = resp.choices.map { choice ->
                    LLMMessage(
                        role = when (choice.message.role) {
                            "system" -> LLMRole.SYSTEM
                            "assistant", "model" -> LLMRole.MODEL
                            else -> LLMRole.USER
                        },
                        content = listOfNotNull(
                            choice.message.content?.let { text -> LLMContent(text = text) },
                            choice.message.toolCalls.firstOrNull()?.let { call ->
                                LLMContent(
                                    functionCall = LLMFunctionCall(
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
                                )
                            }
                        ),
                    )
                },
                usage = resp.usage?.let { usage ->
                    LLMUsage(
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

    override fun getBuiltInTools(): List<Tool> = listOf(
        DeepseekFetchTool(Fetch()),
        DeepseekWebsearchTool(DuckDuckGoWebsearch())
    )

    protected open fun getFunctionType(function: LLMFunctionDeclaration): String {
        return "function"
    }

    protected open fun getEndpoint(): String {
        return DEEPSEEK_ENDPOINT
    }

    private fun toDSMessage(message: LLMMessage): List<DSMessage> {
        val role = when (message.role) {
            LLMRole.USER -> "user"
            LLMRole.SYSTEM, LLMRole.MODEL -> "system"
        }

        val content = message.content.mapNotNull { item ->
            if (item.text != null) {
                DSContent(
                    type = "text",
                    text = item.text,
                )
            } else if (item.document != null) {
                if (item.document.contentType == MediaType.APPLICATION_PDF) {
                    DSContent(
                        type = "text",
                        text = pdf2Text(item.document.content),
                    )
                } else if (item.document.contentType.toString().startsWith("image/")) {
                    val base64Content = Base64
                        .getEncoder()
                        .encodeToString(IOUtils.toByteArray(item.document.content))

                    DSContent(
                        type = "image_url",
                        image_url = DSImageUrl(
                            url = "data:${item.document.contentType};base64,$base64Content"
                        )
                    )
                } else {
                    throw LLMDocumentTypeNotSupportedException(item.document.contentType)
                }
            } else {
                null
            }
        }

        return listOf(
            DSMessage(
                role = role,
                content = content
            )
        )
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
