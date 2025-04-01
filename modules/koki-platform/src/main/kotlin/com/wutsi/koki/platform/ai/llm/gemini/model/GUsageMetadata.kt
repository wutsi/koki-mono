package com.wutsi.koki.platform.ai.llm.gemini.model

data class GUsageMetadata(
    val promptTokenCount: Int = -1,
    val candidatesTokenCount: Int = -1,
    val totalTokenCount: Int = -1,
)
