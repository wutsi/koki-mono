package com.wutsi.koki.file.dto.event

data class FileDeletedEvent(
    val fileId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
