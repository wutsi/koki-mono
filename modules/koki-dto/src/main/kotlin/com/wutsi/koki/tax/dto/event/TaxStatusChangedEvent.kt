package com.wutsi.koki.tax.dto.event

import com.wutsi.koki.tax.dto.TaxStatus

data class TaxStatusChangedEvent(
    val taxId: Long = -1,
    val tenantId: Long = -1,
    val status: TaxStatus = TaxStatus.UNKNOWN,
    val formId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
