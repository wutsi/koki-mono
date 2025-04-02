package com.wutsi.koki.platform.ai.agent.react

data class Action(
    val name: String = "",
    val reason: String = "",
    val inputs: Map<String, Any> = emptyMap(),
)
