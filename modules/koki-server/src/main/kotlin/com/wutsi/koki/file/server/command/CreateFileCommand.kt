package com.wutsi.koki.file.server.command

import com.wutsi.koki.common.dto.ObjectReference

data class CreateFileCommand(
    val url: String = "",
    val owner: ObjectReference? = null,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
