package com.wutsi.koki.platform.ai.llm.gemini.model

data class GCandidates(
    val content: GContent = GContent(),
    val finishReason: String? = null,
)
