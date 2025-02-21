package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.BusinessTaxIdentifierEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BusinessTaxIdentifierRepository : CrudRepository<BusinessTaxIdentifierEntity, Long> {
    fun findByBusinessId(businessId: Long): List<BusinessTaxIdentifierEntity>
    fun findByBusinessIdAndSalesTaxId(businessId: Long, salesTaxId: Long): BusinessTaxIdentifierEntity?
}
