package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigurationRepository : CrudRepository<ConfigurationEntity, Long> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): ConfigurationEntity?

    fun findByTenantId(tenantId: Long): List<ConfigurationEntity>

    fun findByTenantIdAndNameIn(tenantId: Long, names: List<String>): List<ConfigurationEntity>
}
