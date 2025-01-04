package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.AttributeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttributeRepository : CrudRepository<AttributeEntity, Long> {
    fun findByTenantId(tenantId: Long): List<AttributeEntity>

    fun findByTenantIdAndNameIn(tenantId: Long, name: List<String>): List<AttributeEntity>
}
