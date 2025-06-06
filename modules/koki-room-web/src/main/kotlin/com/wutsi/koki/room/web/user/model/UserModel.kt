package com.wutsi.koki.room.web.user.model

import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import java.util.Date

data class UserModel(
    val id: Long = -1,
    val username: String = "",
    val accountId: Long? = null,
    val email: String = "",
    val displayName: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val type: UserType = UserType.UNKNOWN,
    val language: String? = null,
    val languageText: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
)
