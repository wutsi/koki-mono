package com.wutsi.koki.platform.ai.genai.gemini.model

data class GContent(
    val role: String = "",
    val parts: List<GPart> = emptyList(),
)
