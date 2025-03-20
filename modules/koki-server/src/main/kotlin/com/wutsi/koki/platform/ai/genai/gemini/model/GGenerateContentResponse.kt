package com.wutsi.koki.platform.ai.genai.gemini.model

data class GGenerateContentResponse(
    val candidates: List<GCandidates> = emptyList(),
)
