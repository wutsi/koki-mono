package com.wutsi.koki.platform.ai.llm

data class LLMFunctionCall(
    val name: String = "",
    val args: Map<String, String> = emptyMap(),
)
