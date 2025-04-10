package com.wutsi.koki.tax.dto

import java.util.Date

data class TaxFile(
    val id: Long = -1,
    val data: TaxFileData = TaxFileData(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
