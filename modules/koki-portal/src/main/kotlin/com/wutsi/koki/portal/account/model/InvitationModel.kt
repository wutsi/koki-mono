package com.wutsi.koki.portal.account.model

import java.util.Date

data class InvitationModel(
    val id: String = "",
    val accountId: Long = -1,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdAtMoment: String = "",
)
