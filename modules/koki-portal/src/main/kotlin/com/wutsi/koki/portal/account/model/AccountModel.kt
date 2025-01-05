package com.wutsi.koki.portal.account.model

import com.wutsi.koki.portal.model.UserModel
import java.util.Date

data class AccountModel(
    val id: Long = -1,
    val accountType: AccountTypeModel? = null,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val website: String? = null,
    val language: String? = null,
    val languageText: String? = null,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val managedBy: UserModel? = null,
    val attributes: List<AccountAttributeModel> = emptyList()
)
