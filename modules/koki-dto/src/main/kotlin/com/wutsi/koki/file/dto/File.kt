package com.wutsi.koki.file.dto

import com.wutsi.koki.common.dto.ObjectType
import java.util.Date

data class File(
    val id: Long = -1L,
    val fileType: ObjectType = ObjectType.UNKNOWN,
    val createdById: Long? = null,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val title: String? = null,
    val description: String? = null,
    val language: String? = null,
    val numberOfPages: Int? = null,
    val labels: List<LabelSummary> = emptyList(),
)
