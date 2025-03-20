package com.wutsi.koki.platform.ai.genai

data class Message(
    val role: String = "",
    val text: String? = null,
    val document: Document? = null,
)
