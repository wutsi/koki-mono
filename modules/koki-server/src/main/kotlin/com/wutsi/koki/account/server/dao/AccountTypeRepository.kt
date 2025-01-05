package com.wutsi.koki.account.server.dao

import com.wutsi.koki.tenant.server.domain.AccountTypeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountTypeRepository : CrudRepository<AccountTypeEntity, Long> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): AccountTypeEntity?
}
