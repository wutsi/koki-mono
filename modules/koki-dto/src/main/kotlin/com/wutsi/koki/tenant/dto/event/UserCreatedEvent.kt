package com.wutsi.koki.tenant.dto.event

data class UserCreatedEvent(
    val userId: Long = -1,
    val invitationId: String? = null,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
)
