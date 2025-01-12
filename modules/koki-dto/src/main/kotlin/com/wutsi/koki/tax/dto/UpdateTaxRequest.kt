package com.wutsi.koki.tax.dto

import java.util.Date

data class UpdateTaxRequest(
    val fiscalYear: Int = -1,
    val taxTypeId: Long = -1,
    val description: String? = null,
    val startAt: Date? = null,
    val dueAt: Date? = null,
    val accountantId: Long? = null,
)
