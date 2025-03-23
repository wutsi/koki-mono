package com.wutsi.koki.platform.ai.genai

interface GenAIService {
    @Throws(GenAIException::class)
    fun generateContent(request: GenAIRequest): GenAIResponse
}
