package com.wutsi.koki.tax.server.dao

import com.wutsi.koki.tax.server.domain.TaxTypeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaxTypeRepository : CrudRepository<TaxTypeEntity, Long> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): TaxTypeEntity?
}
