package com.wutsi.koki.workflow.dto

import java.util.Date

data class File(
    val id: Long = -1,
    val name: String = "",
    val description: String? = null,
    val url: String = "",
    val contentType: String? = null,
    val contentLength: Long = -1,
    val contentLanguage: String? = null,
    val createdAt: Date = Date(),
    val createdByUserId: Long = -1,
)
