package com.wutsi.koki.payment.server.dao

import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.server.domain.TransactionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : CrudRepository<TransactionEntity, String> {
    fun findByInvoiceIdAndStatus(invoiceId: Long, status: TransactionStatus): List<TransactionEntity>
}
