package com.wutsi.koki.portal.account.form

data class AccountForm(
    val accountTypeId: Long = -1,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val website: String? = null,
    val language: String? = null,
    val description: String? = null,
    val managedById: Long = -1,
    val attributes: Map<Long, String> = emptyMap(),
)
