package com.wutsi.koki.service.server.dao

import com.wutsi.koki.service.server.domain.ServiceEntity
import org.springframework.data.repository.CrudRepository

interface ServiceRepository : CrudRepository<ServiceEntity, String> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): ServiceEntity?
}
