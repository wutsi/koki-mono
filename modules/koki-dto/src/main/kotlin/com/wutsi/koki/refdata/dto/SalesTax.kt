package com.wutsi.koki.refdata.dto

data class SalesTax(
    val id: Long = -1,
    val name: String = "",
    val stateId: Long? = null,
    val country: String = "",
    val rate: Double = 0.0,
    val active: Boolean = true,
    val priority: Int = 0,
)
