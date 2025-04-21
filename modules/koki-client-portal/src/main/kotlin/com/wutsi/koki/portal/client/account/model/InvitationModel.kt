package com.wutsi.koki.portal.client.account.model

import java.util.Date

data class InvitationModel(
    val id: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val account: AccountModel = AccountModel(),
)
