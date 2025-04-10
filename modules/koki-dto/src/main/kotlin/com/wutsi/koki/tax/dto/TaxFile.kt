package com.wutsi.koki.tax.dto

import java.util.Date

data class TaxFile(
    val fileId: Long = -1,
    val data: TaxFileData = TaxFileData(),
    val createAt: Date = Date(),
    val modifiedAt: Date = Date()
)
