package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.InvitationModel
import com.wutsi.koki.tenant.dto.Invitation
import com.wutsi.koki.tenant.dto.InvitationSummary
import org.springframework.stereotype.Service

@Service
class InvitationMapper : TenantAwareMapper() {
    fun toInvitationModel(entity: Invitation): InvitationModel {
        val df = createDateFormat()
        return InvitationModel(
            id = entity.id,
            createdById = entity.createdById,
            displayName = entity.displayName,
            email = entity.email,
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            expiresAt = entity.expiresAt,
            expiresAtText = df.format(entity.expiresAt),
            status = entity.status,
            type = entity.type,
            language = entity.language,
        )
    }

    fun toInvitationModel(entity: InvitationSummary): InvitationModel {
        val df = createDateFormat()
        return InvitationModel(
            id = entity.id,
            createdById = entity.createdById,
            displayName = entity.displayName,
            email = entity.email,
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            expiresAt = entity.expiresAt,
            expiresAtText = df.format(entity.expiresAt),
            status = entity.status,
            type = entity.type,
        )
    }
}
