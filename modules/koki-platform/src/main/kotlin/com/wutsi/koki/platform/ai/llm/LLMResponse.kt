package com.wutsi.koki.platform.ai.llm

class LLMResponse(
    val messages: List<Message> = emptyList(),
    val usage: Usage? = null,
)
