package com.wutsi.koki.platform.ai.llm

data class LLMMessage(
    val role: LLMRole = LLMRole.USER,
    val content: List<LLMContent> = emptyList(),
)
