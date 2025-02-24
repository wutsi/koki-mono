package com.wutsi.koki.invoice.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.Date

data class CreateInvoiceRequest(
    val taxId: Long? = null,
    val orderId: Long? = null,

    val customerAccountId: Long? = null,
    @get:NotEmpty val customerName: String = "",
    @get:NotEmpty @get:Email val customerEmail: String = "",
    val customerPhone: String? = null,
    val customerMobile: String? = null,

    @get:NotEmpty @get:Size(min = 3, max = 3) val currency: String = "",

    val dueAt: Date? = null,
    val description: String? = null,

    @get:Size(max = 30) val shippingPostalCode: String? = null,
    @get:Size(max = 2) val shippingCountry: String? = null,
    val shippingStreet: String? = null,
    val shippingCityId: Long? = null,

    @get:Size(max = 30) val billingPostalCode: String? = null,
    @get:Size(max = 2) val billingCountry: String? = null,
    val billingStreet: String? = null,
    val billingCityId: Long? = null,

    @get:NotEmpty val items: List<Item> = emptyList(),
)
