package com.wutsi.koki.portal.lead.service

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.portal.lead.form.LeadForm
import com.wutsi.koki.portal.lead.mapper.LeadMapper
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.sdk.KokiLeads
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Collections

@Service
class LeadService(
    private val koki: KokiLeads,
    private val mapper: LeadMapper,
    private val listingService: ListingService,
) {
    fun get(id: Long): LeadModel {
        val lead: Lead = koki.get(id).lead
        val listing = lead.listingId?.let { id -> listingService.get(id) }
        return mapper.toLeadModel(lead, listing)
    }

    fun search(
        ids: List<Long> = Collections.emptyList(),
        listingIds: List<Long> = Collections.emptyList(),
        agentUserIds: List<Long> = Collections.emptyList(),
        statuses: List<LeadStatus> = Collections.emptyList(),
        keywords: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true
    ): List<LeadModel> {
        val leads = koki.search(
            ids = ids,
            listingIds = listingIds,
            agentUserIds = agentUserIds,
            statuses = statuses,
            keywords = keywords,
            limit = limit,
            offset = offset,
        ).leads

        val listingIds = leads.mapNotNull { lead -> lead.listingId }
        val listings = if (listingIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            listingService.search(
                ids = listingIds,
                limit = limit,
            ).items.associateBy { listing -> listing.id }
        }

        return leads.map { lead -> mapper.toLeadModel(lead, listings) }
    }

    fun updateStatus(form: LeadForm) {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        koki.updateStatus(
            id = form.id,
            request = UpdateLeadStatusRequest(
                status = form.status ?: LeadStatus.UNKNOWN,
                nextContactAt = form.nextContactAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                nextVisitAt = form.nextVisitAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
            )
        )
    }
}
