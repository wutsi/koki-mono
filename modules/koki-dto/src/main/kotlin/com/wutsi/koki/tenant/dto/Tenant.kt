package com.wutsi.koki.tenant.dto

import java.util.Date

data class Tenant(
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
    val logoUrl: String? = null,
    val iconUrl: String? = null,
    val portalUrl: String = "",
    val websiteUrl: String? = null,
    val status: TenantStatus = TenantStatus.ACTIVE,
    val createdAt: Date = Date(),
    val moduleIds: List<Long> = emptyList(),
)
