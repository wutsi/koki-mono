package com.wutsi.koki.portal.tenant.model

import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.module.model.PermissionModel
import com.wutsi.koki.tenant.dto.TenantStatus
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
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
    val iconUrl: String? = null,
    val portalUrl: String = "",
    val websiteUrl: String? = null,
    val createdAt: Date = Date(),
    val modules: List<ModuleModel> = emptyList()
) {
    val permissions: List<PermissionModel>
        get() = modules.flatMap { module -> module.permissions }
            .sortedBy { permission -> permission.name }

    fun hasModule(name: String): Boolean {
        return modules.find { module -> module.name == name } != null
    }

    fun createMoneyFormat(): NumberFormat {
        return DecimalFormat(monetaryFormat)
    }

    fun createDateFormat(): DateFormat {
        return SimpleDateFormat(dateFormat)
    }

    fun createDateTimeFormat(): DateFormat {
        return SimpleDateFormat(dateTimeFormat)
    }
}
