package com.wutsi.koki.platform.ai.genai

interface GenAIService {
    fun generateContent(request: GenAIRequest): GenAIResponse
}
