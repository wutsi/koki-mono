package com.wutsi.koki.portal.lead.mapper

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.lead.model.LeadMessageModel
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class LeadMapper : TenantAwareMapper() {
    fun toLeadModel(
        entity: Lead,
        listing: ListingModel?,
        user: UserModel,
        message: LeadMessageModel,
    ): LeadModel {
        val df = createDateTimeFormat()
        return LeadModel(
            id = entity.id,
            listing = listing,
            user = user,
            lastMessage = message,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = df.format(entity.modifiedAt),
            nextContactAt = entity.nextContactAt,
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            totalMessages = entity.totalMessages,
        )
    }

    fun toLeadModel(
        entity: LeadSummary,
        listings: Map<Long, ListingModel>,
        users: Map<Long, UserModel>,
        messages: Map<Long, LeadMessageModel>,
    ): LeadModel {
        val df = createDateTimeFormat()
        return LeadModel(
            id = entity.id,
            listing = entity.listingId?.let { id -> listings[id] },
            user = users[entity.userId] ?: UserModel(entity.userId),
            lastMessage = messages[entity.lastMessageId] ?: LeadMessageModel(entity.lastMessageId),
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = df.format(entity.modifiedAt),
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
        )
    }
}
