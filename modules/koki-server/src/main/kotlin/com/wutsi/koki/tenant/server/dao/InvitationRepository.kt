package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.BusinessEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BusinessRepository : CrudRepository<BusinessEntity, Long> {
    fun findByTenantId(tenantId: Long): BusinessEntity?
}
