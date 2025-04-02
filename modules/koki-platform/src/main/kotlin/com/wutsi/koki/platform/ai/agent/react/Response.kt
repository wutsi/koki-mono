package com.wutsi.koki.platform.ai.agent.react

data class Response(
    val thought: String = "",
    val answer: String? = null,
    val action: Action? = null,
)
