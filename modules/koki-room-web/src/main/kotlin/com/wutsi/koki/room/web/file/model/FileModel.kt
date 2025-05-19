package com.wutsi.koki.room.web.file.model

import com.wutsi.koki.file.dto.FileType

data class FileModel(
    val id: Long = -1,
    val type: FileType = FileType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val contentUrl: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val contentLengthText: String = "",
    val extension: String = "",
)
