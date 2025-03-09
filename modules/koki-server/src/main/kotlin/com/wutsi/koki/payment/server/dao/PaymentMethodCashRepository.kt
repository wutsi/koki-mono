package com.wutsi.koki.payment.server.dao

import com.wutsi.koki.payment.server.domain.PaymentMethodCashEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodCashRepository : CrudRepository<PaymentMethodCashEntity, String> {
    fun findByTransactionId(transactionId: String): PaymentMethodCashEntity?
}
