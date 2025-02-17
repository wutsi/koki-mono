package com.wutsi.koki.tax.server.dao

import com.wutsi.koki.tax.server.domain.TaxProductEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaxProductRepository : CrudRepository<TaxProductEntity, Long> {
    fun findByTaxIdAndProductId(taxId: Long, productId: Long): TaxProductEntity?

    fun findByTaxIdAndTenantId(taxId: Long, tenantId: Long, pageable: Pageable): List<TaxProductEntity>
}
