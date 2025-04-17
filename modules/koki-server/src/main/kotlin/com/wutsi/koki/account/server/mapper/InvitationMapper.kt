package com.wutsi.koki.account.server.mapper

import com.wutsi.koki.account.dto.Invitation
import com.wutsi.koki.account.server.domain.InvitationEntity
import org.springframework.stereotype.Service

@Service
class InvitationMapper {
    fun toInvitation(entity: InvitationEntity): Invitation {
        return Invitation(
            id = entity.id ?: "",
            accountId = entity.accountId,
            createdAt = entity.createdAt,
            createdById = entity.createById
        )
    }
}
