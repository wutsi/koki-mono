package com.wutsi.koki.portal.ai.form

data class AISettingsForm(
    val provider: String? = null,
    val geminiApiKey: String? = null,
    val geminiModel: String? = null,
    val deepseekApiKey: String? = null,
    val deepseekModel: String? = null,
)
