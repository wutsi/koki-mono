package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.AttributeEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttributeRepository : CrudRepository<AttributeEntity, Long> {
    @Query("SELECT A FROM AttributeEntity A WHERE A.tenant.id=?1 ORDER BY A.name")
    fun findByTenantId(tenantId: Long): List<AttributeEntity>

    @Query("SELECT A FROM AttributeEntity A WHERE A.tenant.id=?1 AND A.name IN  ?2")
    fun findByTenantIdAndNameIn(tenantId: Long, name: List<String>): List<AttributeEntity>
}
