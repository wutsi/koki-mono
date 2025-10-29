package com.wutsi.koki.portal.user.model

import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import java.util.Date

data class InvitationModel(
    val id: String = "",
    val createdById: Long? = null,
    val status: InvitationStatus = InvitationStatus.UNKNOWN,
    val type: InvitationType = InvitationType.UNKNOWN,
    val email: String = "",
    val displayName: String = "",
    val language: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val expiresAt: Date = Date(),
    val expiresAtText: String = "",
)
