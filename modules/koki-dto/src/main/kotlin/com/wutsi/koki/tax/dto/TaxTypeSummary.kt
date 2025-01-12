package com.wutsi.koki.tax.dto

import java.util.Date

data class TaxTypeSummary(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
