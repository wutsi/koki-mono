package com.wutsi.koki.platform.ai.llm.deepseek.model

import com.wutsi.koki.platform.ai.llm.deepseek.DSFunction

data class DSTool(
    val type: String = "function",
    val function: DSFunction,
)
