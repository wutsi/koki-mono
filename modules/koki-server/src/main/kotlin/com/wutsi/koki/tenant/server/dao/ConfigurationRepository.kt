package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.AttributeEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigurationRepository : CrudRepository<ConfigurationEntity, Long> {
    @Query("SELECT C FROM ConfigurationEntity C WHERE C.attribute.tenant.id=?1 ORDER BY C.attribute.name")
    fun findByTenantId(tenantId: Long): List<ConfigurationEntity>

    @Query("SELECT C FROM ConfigurationEntity C WHERE C.attribute.tenant.id=?1 AND C.attribute.name IN  ?2")
    fun findByTenantIdAndNameIn(tenantId: Long, names: List<String>): List<ConfigurationEntity>

    fun findByAttributeIn(attribute: List<AttributeEntity>): List<ConfigurationEntity>
}
