package com.wutsi.koki.platform.ai.llm

interface LLM {
    fun models(): List<String>

    @Throws(LLMException::class)
    fun generateContent(request: LLMRequest): LLMResponse
}
