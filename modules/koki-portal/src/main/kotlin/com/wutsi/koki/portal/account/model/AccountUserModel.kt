package com.wutsi.koki.portal.account.model

import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class AccountUserModel(
    val id: Long = -1,
    val username: String = "",
    val status: UserStatus = UserStatus.NEW,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
)
