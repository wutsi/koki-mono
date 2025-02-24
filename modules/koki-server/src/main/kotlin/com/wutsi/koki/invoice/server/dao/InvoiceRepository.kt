package com.wutsi.koki.invoice.server.dao

import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : CrudRepository<InvoiceEntity, Long>
