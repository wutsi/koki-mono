package com.wutsi.koki.platform.ai.llm.gemini.model

import com.wutsi.koki.platform.ai.gemini.model.GFunctionCall

data class GPart(
    val text: String? = null,
    val inlineData: GInlineData? = null,
    val functionCall: GFunctionCall? = null
)
