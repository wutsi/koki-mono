package com.wutsi.koki.lead.server.mapper

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary
import com.wutsi.koki.lead.server.domain.LeadEntity
import org.springframework.stereotype.Service

@Service
class LeadMapper {
    fun toLead(entity: LeadEntity): Lead {
        return Lead(
            id = entity.id ?: -1,
            listingId = entity.listing?.id,
            deviceId = entity.deviceId,
            userId = entity.userId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            message = entity.message,
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            nextContactAt = entity.nextContactAt,
            nextVisitAt = entity.nextVisitAt,
        )
    }

    fun toLeadSummary(entity: LeadEntity): LeadSummary {
        return LeadSummary(
            id = entity.id ?: -1,
            listingId = entity.listing?.id ?: -1,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            nextContactAt = entity.nextContactAt,
            nextVisitAt = entity.nextVisitAt,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
        )
    }
}
