package com.wutsi.koki.portal.lead.mapper

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSummary
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.model.ListingModel
import org.springframework.stereotype.Service

@Service
class LeadMapper : TenantAwareMapper() {
    fun toLeadModel(entity: Lead, listing: ListingModel?): LeadModel {
        val df = createDateFormat()
        return LeadModel(
            id = entity.id,
            listing = listing,
            firstName = entity.firstName,
            lastName = entity.lastName,
            displayName = "${entity.firstName} ${entity.lastName}".trim(),
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            phoneNumberFormatted = entity.phoneNumber.ifEmpty { null }?.let { number -> formatPhoneNumber(number) },
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            nextContactAt = entity.nextContactAt,
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
            message = entity.message,
        )
    }

    fun toLeadModel(entity: LeadSummary, listings: Map<Long, ListingModel>): LeadModel {
        val df = createDateFormat()
        return LeadModel(
            id = entity.id,
            listing = entity.listingId?.let { id -> listings[id] },
            firstName = entity.firstName,
            lastName = entity.lastName,
            displayName = "${entity.firstName} ${entity.lastName}".trim(),
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            phoneNumberFormatted = entity.phoneNumber.ifEmpty { null }?.let { number -> formatPhoneNumber(number) },
            visitRequestedAt = entity.visitRequestedAt,
            status = entity.status,
            source = entity.source,
            modifiedAt = entity.modifiedAt,
            nextContactAt = entity.nextContactAt,
            nextContactAtText = entity.nextContactAt?.let { date -> df.format(date) },
            nextVisitAt = entity.nextVisitAt,
            nextVisitAtText = entity.nextVisitAt?.let { date -> df.format(date) },
            createdAt = entity.createdAt,
            createdAtText = df.format(entity.createdAt),
        )
    }
}
