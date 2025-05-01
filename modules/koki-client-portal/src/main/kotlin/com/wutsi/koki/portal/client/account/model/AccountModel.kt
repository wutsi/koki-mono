package com.wutsi.koki.portal.client.account.model

import java.util.Date

data class AccountModel(
    val id: Long = -1,
    val userId: Long? = null,
    val name: String = "",
    val email: String? = null,
    val language: String? = null,
    val languageText: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val invitationId: String? = null,
)
