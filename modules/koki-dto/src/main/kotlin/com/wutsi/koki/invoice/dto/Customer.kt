package com.wutsi.koki.invoice.dto

data class Customer(
    val accountId: Long? = null,

    val name: String = "",
    val email: String = "",

    val phone: String? = null,
    val mobile: String? = null,
)
