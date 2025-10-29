package com.wutsi.koki.tenant.dto

import java.util.Date

data class Invitation(
    val id: String = "",
    val createdById: Long? = null,
    val status: InvitationStatus = InvitationStatus.UNKNOWN,
    val type: InvitationType = InvitationType.UNKNOWN,
    val email: String = "",
    val language: String? = null,
    val displayName: String = "",
    val createdAt: Date = Date(),
    val expiresAt: Date = Date(),
)
