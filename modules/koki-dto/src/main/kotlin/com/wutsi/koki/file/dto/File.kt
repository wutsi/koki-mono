package com.wutsi.koki.file.dto

import java.util.Date

data class File(
    val id: String = "",
    val createdById: Long? = null,
    val workflowInstanceId: String? = null,
    val formId: String? = null,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
