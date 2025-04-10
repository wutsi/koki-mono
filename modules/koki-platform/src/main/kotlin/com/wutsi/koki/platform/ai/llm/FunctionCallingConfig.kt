package com.wutsi.koki.platform.ai.llm

data class FunctionCallingConfig(
    val mode: FunctionCallingMode = FunctionCallingMode.AUTO,
    val allowedFunctionNames: List<String>? = null,
)
