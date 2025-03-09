package com.wutsi.koki.payment.dto

data class GetTransactionResponse(
    val transaction: Transaction = Transaction()
)
