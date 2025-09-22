package com.wutsi.koki.file.dto.event

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.file.dto.FileType

data class FileUploadedEvent(
    val fileId: Long = -1,
    val tenantId: Long = -1,
    val fileType: FileType = FileType.UNKNOWN,
    val owner: ObjectReference? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
