package com.wutsi.koki.portal.ai.form

data class AISettingsForm(
    val model: String? = null,
    val geminiApiKey: String? = null,
    val geminiModel: String? = null,
)
