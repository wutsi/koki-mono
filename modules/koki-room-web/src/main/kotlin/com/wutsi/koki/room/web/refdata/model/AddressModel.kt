package com.wutsi.koki.room.web.refdata.model

import com.wutsi.koki.platform.util.HtmlUtils

data class AddressModel(
    val street: String? = null,
    val postalCode: String? = null,
    val city: LocationModel? = null,
    val state: LocationModel? = null,
    val country: String? = null,
    val countryName: String? = null,
) {
    val text: String
        get() = toString()

    fun toHtml(): String {
        return HtmlUtils.toHtml(toString())
    }

    override fun toString(): String {
        return listOf(
            street?.ifEmpty { null },
            listOf(city?.name, state?.name).filterNotNull().joinToString(", ").ifEmpty { null },
            postalCode?.ifEmpty { null },
            countryName?.ifEmpty { null }
        ).filterNotNull().joinToString("\n")
    }
}
