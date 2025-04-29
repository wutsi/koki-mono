package com.wutsi.koki.account.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateAccountRequest(
    val accountTypeId: Long? = null,

    @get:NotEmpty @get:Size(max = 100) val name: String = "",
    @get:Size(max = 30) val phone: String? = null,
    @get:Size(max = 30) val mobile: String? = null,
    @get:NotEmpty @get:Email @get:Size(max = 255) val email: String = "",
    @get:Size(max = 2) val language: String? = null,

    val website: String? = null,
    val description: String? = null,
    val attributes: Map<Long, String> = emptyMap(),
    val managedById: Long? = null,

    @get:Size(max = 30) val shippingPostalCode: String? = null,
    @get:Size(max = 2) val shippingCountry: String? = null,
    val shippingStreet: String? = null,
    val shippingCityId: Long? = null,

    @get:Size(max = 30) val billingPostalCode: String? = null,
    @get:Size(max = 2) val billingCountry: String? = null,
    val billingStreet: String? = null,
    val billingCityId: Long? = null,
    val billingSameAsShippingAddress: Boolean = false,
)
