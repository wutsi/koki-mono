package com.wutsi.koki.portal.pub.file.model

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType

data class FileModel(
    val id: Long = -1,
    val type: FileType = FileType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val description: String? = null,
    val language: String? = null,
    val languageText: String? = null,
    val status: FileStatus = FileStatus.UNKNOWN,
)
