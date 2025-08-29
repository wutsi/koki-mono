package com.wutsi.blog.portal.common.model

data class MoneyModel(
    val amount: Double = 0.0,
    val currency: String = "",
    val text: String = "",
) {
    val free: Boolean
        get() = (amount == 0.0)

    override fun toString(): String {
        return text
    }
}
