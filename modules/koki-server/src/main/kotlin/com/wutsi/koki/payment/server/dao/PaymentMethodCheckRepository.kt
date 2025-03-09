package com.wutsi.koki.payment.server.dao

import com.wutsi.koki.payment.server.domain.PaymentMethodCheckEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodCheckRepository : CrudRepository<PaymentMethodCheckEntity, String> {
    fun findByTransactionId(transactionId: String): PaymentMethodCheckEntity?
}
