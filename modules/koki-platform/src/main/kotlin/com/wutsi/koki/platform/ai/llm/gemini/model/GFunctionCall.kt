package com.wutsi.koki.platform.ai.gemini.model

data class GFunctionCall(
    val name: String = "",
    val args: Map<String, String> = emptyMap(),
)
