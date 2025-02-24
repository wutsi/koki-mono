package com.wutsi.koki.invoice.server.dao

import com.wutsi.koki.invoice.server.domain.InvoiceSequenceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceSequenceRepository : CrudRepository<InvoiceSequenceEntity, Long> {
    fun findByTenantId(tenantId: Long): InvoiceSequenceEntity?
}
