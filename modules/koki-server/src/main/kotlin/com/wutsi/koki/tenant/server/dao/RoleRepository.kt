package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : CrudRepository<RoleEntity, Long> {
    @Query("SELECT R FROM RoleEntity R WHERE R.tenant.id=?1 ORDER BY R.name")
    fun findByTenantId(tenantId: Long): List<RoleEntity>

    @Query("SELECT R FROM RoleEntity R WHERE R.tenant.id=?1 AND R.name IN ?2")
    fun findByTenantIdAndNameIn(tenantId: Long, name: List<String>): List<RoleEntity>

    fun findByIdInAndTenantId(id: List<Long>, tenantId: Long): List<RoleEntity>
}
