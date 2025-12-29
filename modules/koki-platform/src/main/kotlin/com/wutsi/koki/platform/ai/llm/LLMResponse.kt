package com.wutsi.koki.platform.ai.llm

class LLMResponse(
    val messages: List<LLMMessage> = emptyList(),
    val usage: LLMUsage? = null,
)
