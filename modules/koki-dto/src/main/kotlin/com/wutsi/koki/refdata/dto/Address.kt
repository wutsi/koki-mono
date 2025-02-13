package com.wutsi.koki.refdata.dto

data class Address(
    val street: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
    val stateId: Long? = null,
)
