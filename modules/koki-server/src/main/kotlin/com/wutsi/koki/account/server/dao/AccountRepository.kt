package com.wutsi.koki.account.server.dao

import com.wutsi.koki.form.server.domain.AccountEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CrudRepository<AccountEntity, Long>
