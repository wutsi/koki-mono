package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.server.dao.InvitationRepository
import com.wutsi.koki.account.server.domain.InvitationEntity
import com.wutsi.koki.email.server.service.EmailService

class AccountInvitationService(
    private val dao: InvitationRepository,
    private val emailService: EmailService,
) {
    fun create(accountId: Long): InvitationEntity {
        TODO()
    }
}
