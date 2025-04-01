package com.wutsi.koki.platform.ai.llm

data class Usage(
    val promptTokenCount: Int = -1,
    val responseTokenCount: Int = -1,
    val totalTokenCount: Int = -1,
)
