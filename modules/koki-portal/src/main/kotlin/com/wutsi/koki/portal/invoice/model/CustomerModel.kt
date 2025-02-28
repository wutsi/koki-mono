package com.wutsi.koki.portal.invoice.model

import com.wutsi.koki.portal.account.model.AccountModel

data class CustomerModel(
    val account: AccountModel? = null,
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val mobile: String? = null,
)
