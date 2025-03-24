package com.wutsi.koki.tax.dto

enum class TaxStatus {
    UNKNOWN,
    NEW,
    GATHERING_DOCUMENTS,
    PROCESSING,
    REVIEWING,
    SUBMITTING,
    DONE,
}
