package com.wutsi.koki.tax.dto

data class TaxFileData(
    val language: String? = null,
    val description: String? = null,
    val numberOfPages: Int? = null,
    val sections: List<TaxFileSection> = emptyList(),
    val contacts: List<TaxFileContact> = emptyList(),
)

data class TaxFileSection(
    val code: String? = null,
    val startPage: Int = -1,
    val endPage: Int = -1,
)

data class TaxFileContact(
    val salutation: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthDate: String? = null,
    val homePhone: String? = null,
    val cellPhone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val zipCode: String? = null,
    val email: String? = null,
    val role: String? = null,
)
