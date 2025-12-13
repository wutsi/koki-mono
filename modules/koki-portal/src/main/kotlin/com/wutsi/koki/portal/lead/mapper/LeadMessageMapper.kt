package com.wutsi.koki.portal.lead.mapper

import com.wutsi.koki.lead.dto.LeadMessage
import com.wutsi.koki.lead.dto.LeadMessageSummary
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.lead.model.LeadMessageModel
import org.springframework.stereotype.Service

@Service
class LeadMessageMapper : TenantAwareMapper() {
    fun toLeadMessageModel(entity: LeadMessage): LeadMessageModel {
        val df = createDateFormat()
        return LeadMessageModel(
            id = entity.id,
            content = entity.content,
            visitRequestedAt = entity.visitRequestedAt,
            visitRequestedAtText = entity.visitRequestedAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
        )
    }

    fun toLeadMessageModel(entity: LeadMessageSummary): LeadMessageModel {
        val df = createDateFormat()
        return LeadMessageModel(
            id = entity.id,
            content = entity.content,
            visitRequestedAt = entity.visitRequestedAt,
            visitRequestedAtText = entity.visitRequestedAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
        )
    }
}
