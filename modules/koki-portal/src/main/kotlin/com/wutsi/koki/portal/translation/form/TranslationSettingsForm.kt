package com.wutsi.koki.portal.translation.form

data class TranslationSettingsForm(
    val aiModel: String? = null,
    val provider: String = "",
    val awsRegion: String? = null,
    val awsAccessKey: String? = null,
    val awsSecretKey: String? = null,
)
