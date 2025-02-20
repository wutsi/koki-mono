package com.wutsi.koki.refdata.dto

data class SalesTax(
    val id: Long = -1,
    val juridictionId: Long? = null,
    val name: String = "",
    val rate: Double = 0.0,
    val priority: Int = 0,
    val active: Boolean = true,
)
