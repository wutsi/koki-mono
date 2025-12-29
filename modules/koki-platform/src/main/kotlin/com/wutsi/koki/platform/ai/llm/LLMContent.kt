package com.wutsi.koki.platform.ai.llm

data class LLMContent(
    val text: String? = null,
    val document: LLMDocument? = null,
    val functionCall: LLMFunctionCall? = null,
)
