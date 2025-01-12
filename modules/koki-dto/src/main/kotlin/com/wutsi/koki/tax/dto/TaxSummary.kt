package com.wutsi.koki.tax.dto

import java.util.Date

data class TaxSummary(
    val id: Long = -1,
    val fiscalYear: Int = -1,
    val taxTypeId: Long? = null,
    val status: TaxStatus = TaxStatus.NEW,
    val accountId: Long = -1,
    val accountantId: Long? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtId: Long? = null,
    val modifiedAtId: Long? = null,
    val startAt: Date? = null,
    val dueAt: Date? = null,
)
