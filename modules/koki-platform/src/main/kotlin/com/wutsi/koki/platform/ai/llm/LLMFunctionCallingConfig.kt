package com.wutsi.koki.platform.ai.llm

data class LLMFunctionCallingConfig(
    val mode: LLMFunctionCallingMode = LLMFunctionCallingMode.AUTO,
    val allowedFunctionNames: List<String>? = null,
)
