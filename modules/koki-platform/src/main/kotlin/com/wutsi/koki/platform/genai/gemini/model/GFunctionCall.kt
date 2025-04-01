package com.wutsi.koki.platform.genai.gemini.model

data class GFunctionCall(
    val name: String = "",
    val args: Map<String, String> = emptyMap(),
)
