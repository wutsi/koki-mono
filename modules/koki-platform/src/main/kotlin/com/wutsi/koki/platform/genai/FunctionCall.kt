package com.wutsi.koki.platform.genai

data class FunctionCall(
    val name: String = "",
    val args: Map<String, String> = emptyMap(),
)
