package com.wutsi.koki.portal.email.model

data class SMTPForm(
    val host: String,
    val port: Int = 587,
    val username: String,
    val password: String,
    val fromAddress: String,
    val fromPersonal: String,
)
