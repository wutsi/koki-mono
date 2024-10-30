package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantRepository : CrudRepository<TenantEntity, Long>
