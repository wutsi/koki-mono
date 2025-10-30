package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Invitation
import com.wutsi.koki.tenant.dto.InvitationSummary
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import org.springframework.stereotype.Service

@Service
class InvitationMapper {
    fun toInvitation(entity: InvitationEntity) = Invitation(
        id = entity.id!!,
        createdById = entity.createdById,
        displayName = entity.displayName,
        email = entity.email,
        createdAt = entity.createdAt,
        expiresAt = entity.expiresAt,
        status = entity.status,
        type = entity.type,
        language = entity.language,
    )

    fun toInvitationSummary(entity: InvitationEntity) = InvitationSummary(
        id = entity.id!!,
        createdById = entity.createdById,
        displayName = entity.displayName,
        email = entity.email,
        createdAt = entity.createdAt,
        expiresAt = entity.expiresAt,
        status = entity.status,
        type = entity.type,
    )
}
