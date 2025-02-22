package com.wutsi.koki.portal.tenant.form

data class BusinessForm(
    val companyName: String = "",
    val phone: String? = null,
    val fax: String? = null,
    val email: String? = null,
    val website: String? = null,
    val addressPostalCode: String? = null,
    val addressCountry: String? = null,
    val addressStreet: String? = null,
    val addressCityId: Long? = null,
    val juridictionIds: List<Long> = emptyList(),
)
