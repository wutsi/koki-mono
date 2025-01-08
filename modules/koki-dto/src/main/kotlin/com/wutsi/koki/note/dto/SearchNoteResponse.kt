package com.wutsi.koki.note.dto

data class SearchNoteResponse(
    val notes: List<NoteSummary> = emptyList()
)
