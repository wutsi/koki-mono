package com.wutsi.koki.tax.dto

import java.util.Date

data class TaxFile(
    val fileId: Long = -1,
    val taxId: Long = -1,
    val data: TaxFileData = TaxFileData(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
