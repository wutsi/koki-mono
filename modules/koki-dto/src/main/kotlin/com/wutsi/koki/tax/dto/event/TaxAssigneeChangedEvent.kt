package com.wutsi.koki.tax.dto.event

data class TaxAssigneeChangedEvent(
    val taxId: Long = -1,
    val tenantId: Long = -1,
    val assigneeId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
