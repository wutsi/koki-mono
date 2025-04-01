package com.wutsi.koki.platform.genai

data class Usage(
    val promptTokenCount: Int = -1,
    val responseTokenCount: Int = -1,
    val totalTokenCount: Int = -1,
)
