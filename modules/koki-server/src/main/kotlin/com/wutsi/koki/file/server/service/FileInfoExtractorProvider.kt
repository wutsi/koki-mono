package com.wutsi.koki.file.server.service

import java.io.File

interface FileInfoExtractor {
    fun extract(file: File): FileInfo
}

data class FileInfo(
    val language: String? = null,
    val numberOfPages: Int? = null,
)
