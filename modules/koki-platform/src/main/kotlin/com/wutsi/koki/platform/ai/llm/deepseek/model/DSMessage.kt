package com.wutsi.koki.platform.ai.llm.deepseek.model

data class DSMessage(
    val role: String,
    val content: List<DSContent>,
)
