package com.wutsi.koki.portal.refdata.model

import com.wutsi.koki.platform.util.HtmlUtils

data class AddressModel(
    val street: String? = null,
    val postalCode: String? = null,
    val city: LocationModel? = null,
    val state: LocationModel? = null,
    val country: String? = null,
    val countryName: String? = null,
    val neighbourhood: LocationModel? = null,
) {
    fun toHtml(includeCountry: Boolean = true): String {
        return HtmlUtils.toHtml(toText(includeCountry))
    }

    fun toText(includeCountry: Boolean = true): String {
        val stateAndPostal = listOf(state?.name, postalCode?.ifEmpty { null })
            .joinToString(" ").ifEmpty { null }

        val cityAndNeighborhood = listOf(
            city?.name,
            if (neighbourhood != null) "(${neighbourhood.name})" else null
        ).joinToString(" ").ifEmpty { null }

        return listOf(
            street?.ifEmpty { null },
            listOf(cityAndNeighborhood, stateAndPostal).filterNotNull().joinToString(", ").ifEmpty { null },
            if (includeCountry) countryName?.ifEmpty { null } else null,
        ).filterNotNull().joinToString("\n")
    }

    override fun toString(): String {
        return toText()
    }
}
