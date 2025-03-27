package com.wutsi.koki.platform.ai.genai

data class Message(
    val role: Role = Role.USER,
    val text: String? = null,
    val document: Document? = null,
)
