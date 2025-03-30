package com.wutsi.koki.payment.server.dao

import com.wutsi.koki.payment.server.domain.PaymentMethodInteracEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodInteracRepository : CrudRepository<PaymentMethodInteracEntity, String> {
    fun findByTransactionId(transactionId: String): PaymentMethodInteracEntity?
}
