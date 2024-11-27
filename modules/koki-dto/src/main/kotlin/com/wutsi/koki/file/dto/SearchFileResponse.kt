package com.wutsi.koki.file.dto

data class SearchFileResponse(
    val files: List<FileSummary> = emptyList()
)
