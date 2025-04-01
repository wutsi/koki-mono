package com.wutsi.koki.platform.ai.llm.gemini.model

data class GContent(
    val role: String = "",
    val parts: List<GPart> = emptyList(),
)
