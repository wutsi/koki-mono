package com.wutsi.koki.account.dto.event

data class InvitationCreatedEvent(
    val invitationId: String = "",
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
