package com.wutsi.koki.account.server.dao

import com.wutsi.koki.account.server.domain.InvitationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvitationRepository : CrudRepository<InvitationEntity, String>
