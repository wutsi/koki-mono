package com.wutsi.koki.tenant.dto

import java.util.Date

data class TenantModel(
    val id: Long = -1,
    val name: String = "",
    val domainName: String = "",
    val locale: String = "",
    val numberFormat: String = "",
    val currency: String = "",
    val currencySymbol: String = "",
    val monetaryFormat: String = "",
    val dateFormat: String = "",
    val timeFormat: String = "",
    val dateTimeFormat: String = "",
    val status: TenantStatus = TenantStatus.ACTIVE,
    val logoUrl: String? = null,
    var iconUrl: String? = null,
    val createdAt: Date = Date(),
)
