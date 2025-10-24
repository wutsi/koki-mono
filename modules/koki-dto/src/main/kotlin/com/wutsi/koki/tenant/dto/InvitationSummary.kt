package com.wutsi.koki.tenant.dto

import java.util.Date

data class InvitationSummary(
    val id: String = "",
    val createdById: Long? = null,
    val status: InvitationStatus = InvitationStatus.UNKNOWN,
    val type: InvitationType = InvitationType.UNKNOWN,
    val email: String = "",
    val displayName: String = "",
    val createdAt: Date = Date(),
    val expiresAt: Date = Date(),
)
