package com.wutsi.koki.tenant.server.command

data class SendUsernameCommand(
    val userId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
