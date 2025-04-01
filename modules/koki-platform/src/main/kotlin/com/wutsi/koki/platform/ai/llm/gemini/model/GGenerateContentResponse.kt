package com.wutsi.koki.platform.ai.llm.gemini.model

data class GGenerateContentResponse(
    val candidates: List<GCandidates> = emptyList(),
    val usageMetadata: GUsageMetadata? = null,
)
