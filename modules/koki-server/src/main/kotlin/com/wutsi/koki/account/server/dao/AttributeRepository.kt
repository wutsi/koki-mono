package com.wutsi.koki.account.server.dao

import com.wutsi.koki.account.server.domain.AttributeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttributeRepository : CrudRepository<AttributeEntity, Long> {
    fun findByTenantIdAndName(tenantId: Long, name: String): AttributeEntity?
}
