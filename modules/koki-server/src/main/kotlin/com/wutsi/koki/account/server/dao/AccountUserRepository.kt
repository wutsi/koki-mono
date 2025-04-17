package com.wutsi.koki.account.server.dao

import com.wutsi.koki.account.server.domain.AccountUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountUserRepository : CrudRepository<AccountUserEntity, Long> {
    fun findByUsernameAndTenantId(username: String, tenantId: Long): AccountUserEntity?
    fun findByAccountIdAndTenantId(accountId: Long, tenantId: Long): AccountUserEntity?
}
