package com.wutsi.koki.chatbot.telegram.tenant.model

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
    val portalUrl: String = "",
    val websiteUrl: String? = null,
    val createdAt: Date = Date(),
    val clientPortalUrl: String = "",
    val country: String = "",
) {
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
