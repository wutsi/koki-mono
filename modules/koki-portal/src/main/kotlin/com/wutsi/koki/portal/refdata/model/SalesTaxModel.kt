package com.wutsi.koki.portal.refdata.model

data class AddressModel(
    val street: String? = null,
    val postalCode: String? = null,
    val city: LocationModel? = null,
    val state: LocationModel? = null,
    val country: String? = null,
    val countryName: String? = null,
) {
    fun toHtml(): String {
        return toString().replace("\n", "<br>")
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
