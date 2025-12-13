package com.wutsi.koki.lead.server.mapper

import com.wutsi.koki.lead.dto.LeadMessage
import com.wutsi.koki.lead.dto.LeadMessageSummary
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import org.springframework.stereotype.Service

@Service
class LeadMessageMapper {
    fun toLeadMessageSummary(entity: LeadMessageEntity) = LeadMessageSummary(
        id = entity.id ?: -1,
        leadId = entity.lead.id ?: -1,
        content = entity.content,
        createdAt = entity.createdAt,
        visitRequestedAt = entity.visitRequestedAt,
    )

    fun toLeadMessage(entity: LeadMessageEntity) = LeadMessage(
        id = entity.id ?: -1,
        leadId = entity.lead.id ?: -1,
        content = entity.content,
        createdAt = entity.createdAt,
        visitRequestedAt = entity.visitRequestedAt,
    )
}
