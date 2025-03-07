package com.wutsi.koki.tax.server.dao

import com.wutsi.koki.tax.server.domain.TaxProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaxProductRepository : CrudRepository<TaxProductEntity, Long> {
    fun findByTaxId(taxId: Long): List<TaxProductEntity>
}
