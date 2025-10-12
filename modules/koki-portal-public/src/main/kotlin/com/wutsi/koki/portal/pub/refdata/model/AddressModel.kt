package com.wutsi.koki.portal.pub.refdata.model

import com.wutsi.koki.platform.util.HtmlUtils

data class AddressModel(
    val street: String? = null,
    val postalCode: String? = null,
    val city: LocationModel? = null,
    val state: LocationModel? = null,
    val neighbourhood: LocationModel? = null,
    val country: String? = null,
    val countryName: String? = null,
) {
    fun toHtml(): String {
        return toHtml(false)
    }

    fun toHtml(includeCountry: Boolean): String {
        return HtmlUtils.toHtml(toText(includeCountry))
    }

    fun toText(includeCountry: Boolean = false): String {
        val stateAndPostal = listOf(state?.name, postalCode?.ifEmpty { null })
            .filterNotNull()
            .joinToString(" ").ifEmpty { null }

        val cityAndNeighborhood = listOf(
            city?.name,
            if (neighbourhood != null) "(${neighbourhood.name})" else null
        )
            .filterNotNull()
            .joinToString(" ").ifEmpty { null }

        return listOf(
            street?.ifEmpty { null },
            listOf(cityAndNeighborhood, stateAndPostal).filterNotNull().joinToString(", ").ifEmpty { null },
            if (includeCountry) countryName?.ifEmpty { null } else null,
        ).filterNotNull().joinToString(", ")
    }

    override fun toString(): String {
        return toText()
    }
}
