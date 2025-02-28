package com.wutsi.koki.invoice.server.dao

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceLogEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceLogRepository : CrudRepository<InvoiceLogEntity, Long> {
    fun findByInvoice(invoice: InvoiceEntity): List<InvoiceLogEntity>
}
