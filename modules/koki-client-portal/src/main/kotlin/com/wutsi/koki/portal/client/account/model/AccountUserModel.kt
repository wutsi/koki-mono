package com.wutsi.koki.portal.client.account.model

import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class AccountUserModel(
    val id: Long = -1,
    val account: AccountModel = AccountModel(),
    val username: String = "",
    val status: UserStatus = UserStatus.NEW,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
) {
    val displayName: String
        get() = account.name

    val email: String
        get() = account.email ?: ""
}
