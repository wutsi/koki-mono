package com.wutsi.koki.file.dto

import java.util.Date

data class FileSummary(
    val id: Long = -1L,
    val type: FileType = FileType.UNKNOWN,
    val status: FileStatus = FileStatus.UNKNOWN,
    val createdById: Long? = null,
    val name: String = "",
    val title: String? = null,
    val titleFr: String? = null,
    val url: String = "",
    val thumbnailUrl: String? = null,
    val previewUrl: String? = null,
    val tinyUrl: String? = null,
    val openGraphUrl: String? = null,
    val contentType: String = "",
    val contentLength: Long = -1,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
