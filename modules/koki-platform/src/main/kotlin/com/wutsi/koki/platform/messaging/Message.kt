package com.wutsi.koki.platform.messaging

import java.io.File

data class Message(
    val sender: Party? = null,
    val recipient: Party,
    val subject: String = "",
    val body: String = "",
    val language: String? = null,
    val mimeType: String = "text/html",
    val attachments: List<File> = emptyList()
)
