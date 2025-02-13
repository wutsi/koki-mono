package com.wutsi.koki.portal.account.form

data class AccountForm(
    val accountTypeId: Long? = null,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val website: String? = null,
    val language: String? = null,
    val description: String? = null,
    val managedById: Long? = null,
    val attributes: Map<Long, String> = emptyMap(),

    val shippingPostalCode: String? = null,
    val shippingCountry: String? = null,
    val shippingStreet: String? = null,
    val shippingCityId: Long? = null,

    val billingPostalCode: String? = null,
    val billingCountry: String? = null,
    val billingStreet: String? = null,
    val billingCityId: Long? = null,
)
