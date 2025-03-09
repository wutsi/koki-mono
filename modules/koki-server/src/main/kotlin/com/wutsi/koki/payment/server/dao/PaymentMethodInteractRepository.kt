package com.wutsi.koki.payment.server.dao

import com.wutsi.koki.payment.server.domain.PaymentMethodInteractEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodInteractRepository : CrudRepository<PaymentMethodInteractEntity, String> {
    fun findByTransactionId(transactionId: String): PaymentMethodInteractEntity?
}
