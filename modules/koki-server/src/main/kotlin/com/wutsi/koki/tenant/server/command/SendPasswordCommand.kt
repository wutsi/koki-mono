package com.wutsi.koki.tenant.server.command

data class SendPasswordCommand(
    val tokenId: String = "",
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
