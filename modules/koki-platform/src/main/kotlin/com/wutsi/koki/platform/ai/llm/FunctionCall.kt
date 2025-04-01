package com.wutsi.koki.platform.ai.llm

data class FunctionCall(
    val name: String = "",
    val args: Map<String, String> = emptyMap(),
)
