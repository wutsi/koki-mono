package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : CrudRepository<RoleEntity, Long> {
    fun findByTenantIdAndNameIn(tenantId: Long, name: List<String>): List<RoleEntity>
}
