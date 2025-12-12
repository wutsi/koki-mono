package com.wutsi.koki.portal.pub.lead.mapper

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.portal.pub.lead.model.LeadModel
import org.springframework.stereotype.Service

@Service
class LeadMapper {
    fun toLeadModel(entity: Lead): LeadModel {
        return LeadModel(
            id = entity.id,
            listingId = entity.listingId,
            userId = entity.userId,
            status = entity.status,
            source = entity.source,
        )
    }
}
