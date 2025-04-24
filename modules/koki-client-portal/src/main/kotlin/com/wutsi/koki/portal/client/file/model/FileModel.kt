package com.wutsi.koki.portal.client.file.model

import java.util.Date

data class FileModel(
    val id: Long = -1,
    val name: String = "",
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val contentLengthText: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
    val extension: String = "",
)
