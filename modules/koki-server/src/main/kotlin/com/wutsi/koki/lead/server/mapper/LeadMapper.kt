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
            agentUserId = entity.agentUserId,
            lastMessageId = entity.lastMessage?.id ?: -1,
            listingId = entity.listing?.id,
            deviceId = entity.deviceId,
            userId = entity.userId,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
            nextContactAt = entity.nextContactAt,
            nextVisitAt = entity.nextVisitAt,
            totalMessages = entity.totalMessages,
        )
    }

    fun toLeadSummary(entity: LeadEntity): LeadSummary {
        return LeadSummary(
            id = entity.id ?: -1,
            agentUserId = entity.agentUserId,
            listingId = entity.listing?.id,
            lastMessageId = entity.lastMessage?.id ?: -1,
            status = entity.status,
            source = entity.source,
            nextContactAt = entity.nextContactAt,
            nextVisitAt = entity.nextVisitAt,
            modifiedAt = entity.modifiedAt,
            createdAt = entity.createdAt,
        )
    }
}
