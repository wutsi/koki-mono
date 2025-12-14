package com.wutsi.koki.platform.ai.llm.deepseek.model

data class DSContent(
    val type: String = "",
    val text: String? = null,
    val image_url: DSImageUrl? = null
)
