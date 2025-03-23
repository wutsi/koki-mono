package com.wutsi.koki.file.dto

import java.util.Date

data class FileSummary(
    val id: Long = -1L,
    val createdById: Long? = null,
    val name: String = "",
    val url: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val labels: List<LabelSummary> = emptyList(),
)
