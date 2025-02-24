package com.wutsi.koki.invoice.server.dao

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceItemRepository : CrudRepository<InvoiceItemEntity, Long> {
    fun findByInvoice(invoice: InvoiceEntity): List<InvoiceItemEntity>
}
