package com.wutsi.koki.portal.lead.service

import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.portal.lead.form.LeadForm
import com.wutsi.koki.portal.lead.mapper.LeadMapper
import com.wutsi.koki.portal.lead.model.LeadModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiLeads
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Collections

@Service
class LeadService(
    private val koki: KokiLeads,
    private val mapper: LeadMapper,
    private val listingService: ListingService,
    private val userService: UserService,
    private val leadMessageService: LeadMessageService,
) {
    fun get(id: Long): LeadModel {
        val lead: Lead = koki.get(id).lead
        val listing = lead.listingId?.let { id -> listingService.get(id) }
        val user = userService.get(lead.userId)
        val message = leadMessageService.get(lead.lastMessageId)
        return mapper.toLeadModel(lead, listing, user, message)
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
                limit = listingIds.size,
            ).items.associateBy { listing -> listing.id }
        }

        val userIds = leads.map { lead -> lead.userId }
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.search(
                ids = userIds,
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        val messageIds = leads.map { lead -> lead.lastMessageId }
        val messages = if (messageIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            leadMessageService.search(
                ids = messageIds,
                limit = messageIds.size,
            ).associateBy { message -> message.id }
        }

        return leads.map { lead -> mapper.toLeadModel(lead, listings, users, messages) }
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
