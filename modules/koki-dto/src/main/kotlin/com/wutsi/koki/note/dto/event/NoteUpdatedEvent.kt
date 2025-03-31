package com.wutsi.koki.note.dto.event

data class NoteUpdatedEvent(
    val noteId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
