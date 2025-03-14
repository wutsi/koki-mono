package com.wutsi.koki.payment.server.service

import com.wutsi.koki.payment.server.domain.TransactionEntity
import kotlin.jvm.Throws

interface PaymentGatewayService {
    @Throws(PaymentGatewayException::class)
    fun checkout(tx: TransactionEntity)

    @Throws(PaymentGatewayException::class)
    fun sync(tx: TransactionEntity)
}
