package com.wutsi.koki.portal.account.model

data class AccountTypeModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val active: Boolean = false,
)
