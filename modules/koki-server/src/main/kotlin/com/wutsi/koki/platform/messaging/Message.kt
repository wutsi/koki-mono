package com.wutsi.koki.platform.messaging

data class Message(
    val sender: Party? = null,
    val recipient: Party,
    val subject: String = "",
    val body: String = "",
    val language: String? = null,
    val mimeType: String = "text/html",
)
