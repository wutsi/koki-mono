package com.wutsi.koki.invoice.server.dao

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import com.wutsi.koki.invoice.server.domain.InvoiceTaxEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceTaxRepository : CrudRepository<InvoiceTaxEntity, Long> {
    fun findByInvoiceItem(item: InvoiceItemEntity): List<InvoiceTaxEntity>

    @Query("SELECT T FROM InvoiceTaxEntity T WHERE T.invoiceItem.invoice = ?1")
    fun findByInvoice(item: InvoiceEntity): List<InvoiceTaxEntity>
}
