package com.wutsi.koki.account.server.dao

import com.wutsi.koki.account.server.domain.AccountInvitationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountInvitationRepository : CrudRepository<AccountInvitationEntity, String>
