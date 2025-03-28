package com.wutsi.koki.file.dto

import java.util.Date

data class File(
    val id: Long = -1L,
    val createdById: Long? = null,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val description: String? = null,
    val language: String? = null,
    val numberOfPages: Int? = null,
    val labels: List<LabelSummary> = emptyList(),
)
