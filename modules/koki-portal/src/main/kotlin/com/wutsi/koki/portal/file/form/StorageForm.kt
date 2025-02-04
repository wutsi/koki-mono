package com.wutsi.koki.portal.file.form

data class FileSettingsForm(
    val type: String = "",
    val s3Bucket: String = "",
    val s3Region: String = "",
    val s3AccessKey: String = "",
    val s3SecretKey: String = "",
)
