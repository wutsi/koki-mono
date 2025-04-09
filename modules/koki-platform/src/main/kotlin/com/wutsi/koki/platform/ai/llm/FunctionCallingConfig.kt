package com.wutsi.koki.platform.ai.llm

data class FunctionCalling(
    val mode: FunctionCallingMode = FunctionCallingMode.AUTO,
        val allowedFunctionNames: List<String> = emptyList(),
)
