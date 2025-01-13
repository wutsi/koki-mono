package com.wutsi.koki.tax.dto

import java.util.Date

data class UpdateTaxRequest(
    val fiscalYear: Int = -1,
    val accountId: Long = -1,
    val taxTypeId: Long? = null,
    val description: String? = null,
    val startAt: Date? = null,
    val dueAt: Date? = null,
    val accountantId: Long? = null,
    val technicianId: Long? = null,
)
