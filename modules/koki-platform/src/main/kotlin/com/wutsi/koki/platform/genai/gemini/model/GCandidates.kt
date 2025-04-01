package com.wutsi.koki.platform.ai.genai.gemini.model

data class GCandidates(
    val content: GContent = GContent(),
    val finishReason: String? = null,
)
