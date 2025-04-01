package com.wutsi.koki.platform.ai.llm

interface LLM {
    @Throws(LLMException::class)
    fun generateContent(request: LLMRequest): LLMResponse
}
