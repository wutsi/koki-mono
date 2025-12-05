package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class ListingLeadCreatedMailet(
    val locationService: LocationService,
    val fileService: FileService,
    val messages: MessageSource,

    private val tenantService: TenantService,
    private val leadService: LeadService,
    private val userService: UserService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
    private val logger: KVLogger,
) : AbstractListingMailet(locationService, fileService, messages) {
    companion object {
        const val SUBJECT = "Listing #{{listingNumber}}: Nouveau client potentiel, à vous de jouer!"
    }

    override fun service(event: Any): Boolean {
        if (event is LeadCreatedEvent) {
            return onLeadCreated(event)
        }
        return false
    }

    private fun onLeadCreated(event: LeadCreatedEvent): Boolean {
        val lead = leadService.get(event.leadId, event.tenantId)
        val listing = lead.listing
        val tenant = tenantService.get(event.tenantId)
        val recipient = lead.listing.sellerAgentUserId?.let { id -> userService.get(id, event.tenantId) } ?: return false

        val data = getData(lead, tenant, recipient)
        val body = templateResolver.resolve("/listing/email/lead-created.html", data)
        val subject = Companion.SUBJECT.replace("{{listingNumber}}", listing.listingNumber.toString())

        return sender.send(
            recipient,
            subject = subject,
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )
    }

    private fun getData(lead: LeadEntity, tenant: TenantEntity, recipient: UserEntity): Map<String, Any> {
        val map = mapOf(
            "leadName" to "${lead.firstName} ${lead.lastName}",
            "leadUrl" to "${tenant.portalUrl}/leads/${lead.id}",
        ).filter { entry -> entry.value != null } as Map<String, Any>
        return map + getListingData(lead.listing, tenant, recipient)
    }
}
