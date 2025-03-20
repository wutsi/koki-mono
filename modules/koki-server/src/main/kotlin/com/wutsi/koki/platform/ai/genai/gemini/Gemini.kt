package com.wutsi.koki.platform.ai.genai.gemini

import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIResponse
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.Message
import com.wutsi.koki.platform.ai.genai.gemini.model.GContent
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerateContentRequest
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerateContentResponse
import com.wutsi.koki.platform.ai.genai.gemini.model.GGenerationConfig
import com.wutsi.koki.platform.ai.genai.gemini.model.GInlineData
import com.wutsi.koki.platform.ai.genai.gemini.model.GPart
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
                    role = message.role,
                    parts = listOf(
                        message.text?.let { text -> GPart(text = text) },
                        message.document?.let { document ->
                            GPart(
                                inlineData = GInlineData(
                                    mimeType = document.contentType.toString(),
                                    data = Base64.getEncoder().encodeToString(document.content)
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
            }
        )
        val resp = rest.postForEntity(
            "https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey",
            req,
            GGenerateContentResponse::class.java
        ).body
        return GenAIResponse(
            messages = resp.candidates
                .flatMap { candidate -> candidate.content.parts }
                .map { part -> Message(text = part.text) }
        )
    }
}
