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
            lastMessageId = entity.lastMessage?.id ?: -1,
            listingId = entity.listing?.id,
            agentUserId = entity.agentUserId,
            userId = entity.userId,
            deviceId = entity.deviceId,
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
            listingId = entity.listing?.id,
            agentUserId = entity.agentUserId,
            userId = entity.userId,
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
