package com.wutsi.koki.portal.refdata.model

data class SalesTaxModel(
    val id: Long = -1,
    val juridictionId: Long = -1,
    val name: String = "",
    val rate: Double = 0.0,
    val priority: Int = 0,
    val active: Boolean = true,
) {
    val rateText: String
        get() = "$rate%"
}
