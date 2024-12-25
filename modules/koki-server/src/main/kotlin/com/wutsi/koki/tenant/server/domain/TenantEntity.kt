package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TENANT")
data class TenantEntity(
    @Id
    val id: Long? = null,

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
    val status: TenantStatus = TenantStatus.ACTIVE,
    val createdAt: Date = Date(),
    val portalUrl: String = "",
)
