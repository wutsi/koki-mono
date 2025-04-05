package com.wutsi.koki.portal.translation.form

data class TranslationSettingsForm(
    val aiProvider: String? = null,
    val provider: String? = null,
    val awsRegion: String? = null,
    val awsAccessKey: String? = null,
    val awsSecretKey: String? = null,
)
