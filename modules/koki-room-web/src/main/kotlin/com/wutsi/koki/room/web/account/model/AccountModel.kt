package com.wutsi.koki.room.web.account.model

import com.wutsi.koki.room.web.tenant.model.TypeModel
import com.wutsi.koki.room.web.user.model.UserModel

data class AccountModel(
    val id: Long = -1,
    val accountType: TypeModel? = null,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String = "",
    val website: String? = null,
    val language: String? = null,
    val languageText: String? = null,
    val description: String? = null,
    val readOnly: Boolean = false,
    val user: UserModel? = null,
)
