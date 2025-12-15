package com.wutsi.koki.platform.ai.llm

data class Message(
    val role: Role = Role.USER,
    val content: List<Content> = emptyList(),
)
