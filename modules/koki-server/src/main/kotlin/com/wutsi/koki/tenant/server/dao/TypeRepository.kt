package com.wutsi.koki.tenant.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.tenant.server.domain.TypeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TypeRepository : CrudRepository<TypeEntity, Long> {
    fun findByNameIgnoreCaseAndObjectTypeAndTenantId(name: String, objectType: ObjectType, tenantId: Long): TypeEntity?
}
