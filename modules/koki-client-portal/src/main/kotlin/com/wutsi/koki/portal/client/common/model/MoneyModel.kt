package com.wutsi.koki.portal.client.common.model

data class MoneyModel(
    val value: Double = 0.0,
    val currency: String = "",
    val text: String = "",
) {
    val free: Boolean
        get() = (value == 0.0)

    override fun toString(): String {
        return text
    }
}
