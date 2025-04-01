package com.wutsi.koki.platform.ai.genai.gemini.model

import com.wutsi.koki.platform.genai.gemini.model.GFunctionCall

data class GPart(
    val text: String? = null,
    val inlineData: GInlineData? = null,
    val functionCall: GFunctionCall? = null
)
