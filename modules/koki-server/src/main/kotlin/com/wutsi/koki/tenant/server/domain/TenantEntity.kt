package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.module.server.domain.ModuleEntity
import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
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
    val websiteUrl: String? = null,
    val clientPortalUrl: String? = null,

    @ManyToMany
    @JoinTable(
        name = "T_TENANT_MODULE",
        joinColumns = arrayOf(JoinColumn(name = "tenant_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "module_fk")),
    )
    val modules: List<ModuleEntity> = mutableListOf(),
) {
    fun createMoneyFormat(): NumberFormat {
        return DecimalFormat(monetaryFormat)
    }

    fun createDateFormat(): DateFormat {
        return SimpleDateFormat(dateFormat)
    }
}
