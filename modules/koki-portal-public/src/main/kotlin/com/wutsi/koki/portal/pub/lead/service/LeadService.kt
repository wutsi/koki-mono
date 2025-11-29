package com.wutsi.koki.portal.pub.lead.service

import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.portal.pub.lead.form.LeadForm
import com.wutsi.koki.portal.pub.lead.mapper.LeadMapper
import com.wutsi.koki.portal.pub.lead.model.LeadModel
import com.wutsi.koki.portal.pub.user.service.UserIdProvider
import com.wutsi.koki.sdk.KokiLeads
import org.springframework.stereotype.Service

@Service
class LeadService(
    private val koki: KokiLeads,
    private val mapper: LeadMapper,
    private val userIdProvider: UserIdProvider,
) {
    fun create(form: LeadForm): Long {
        // Create
        val request = CreateLeadRequest(
            listingId = form.listingId,
            firstName = form.firstName,
            lastName = form.lastName,
            message = form.message,
            email = form.email,
            phoneNumber = form.phoneFull,
            source = LeadSource.WEBSITE,
            userId = userIdProvider.get(),
        )
        val leadId = koki.create(request).leadId

        // Set current user
        val lead = koki.get(leadId).lead
        if (lead.userId == null) {
            userIdProvider.remove()
        } else {
            userIdProvider.set(lead.userId!!)
        }

        return leadId
    }

    fun get(id: Long): LeadModel {
        val lead: Lead = koki.get(id).lead
        return mapper.toLeadModel(lead)
    }
}
