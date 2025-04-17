package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.server.dao.InvitationRepository
import com.wutsi.koki.account.server.domain.InvitationEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class InvitationService(
    private val dao: InvitationRepository,
    private val accountService: AccountService,
    private val securityService: SecurityService,
) {
    fun get(id: String, tenantId: Long): InvitationEntity {
        val invitation = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.INVITATION_NOT_FOUND)) }

        if (invitation.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.INVITATION_NOT_FOUND))
        }
        return invitation
    }

    fun create(request: CreateInvitationRequest, tenantId: Long): InvitationEntity {
         val invitation = dao.save(
            InvitationEntity(
                id = UUID.randomUUID().toString(),
                accountId = request.accountId,
                tenantId = tenantId,
                createdAt = Date(),
                createById = securityService.getCurrentUserIdOrNull(),
            )
        )
        accountService.setInvitation(request.accountId, invitation)
        return invitation
    }
}
