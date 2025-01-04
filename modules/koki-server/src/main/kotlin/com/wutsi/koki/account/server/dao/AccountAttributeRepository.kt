package com.wutsi.koki.account.server.dao

import com.wutsi.koki.form.server.domain.AccountAttributeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountAttributeRepository : CrudRepository<AccountAttributeEntity, Long> {
    fun findByAccountId(accountId: Long): List<AccountAttributeEntity>
}
