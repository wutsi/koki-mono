package com.wutsi.koki.tax.dto

import java.util.Date

data class CreateTaxRequest(
    val fiscalYear: Int = -1,
    val description: String? = null,
    val taxTypeId: Long? = null,
    val accountId: Long = -1,
    val accountantId: Long? = null,
    val technicianId: Long? = null,
    val assigneeId: Long? = null,
    val startAt: Date? = null,
    val dueAt: Date? = null,
)
