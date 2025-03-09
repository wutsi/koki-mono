package com.wutsi.koki.payment.dto.event

import com.wutsi.koki.payment.dto.TransactionStatus

data class TransactionCompletedEvent(
    val transactionId: String = "",
    val tenantId: Long = -1,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
)
