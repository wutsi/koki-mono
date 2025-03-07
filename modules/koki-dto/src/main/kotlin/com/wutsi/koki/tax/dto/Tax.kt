package com.wutsi.koki.tax.dto

import java.util.Date

data class Tax(
    val id: Long = -1,
    val fiscalYear: Int = -1,
    val description: String? = null,
    val totalRevenue: Double? = null,
    val currency: String? = null,
    var productCount: Int = 0,
    val taxTypeId: Long? = null,
    val accountId: Long = -1,
    val accountantId: Long? = null,
    val technicianId: Long? = null,
    val assigneeId: Long? = null,
    val status: TaxStatus = TaxStatus.NEW,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val startAt: Date? = null,
    val dueAt: Date? = null,
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
