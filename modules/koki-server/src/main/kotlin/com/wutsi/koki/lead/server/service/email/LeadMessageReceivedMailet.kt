package com.wutsi.koki.lead.server.service.email

import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.listing.server.service.email.AbstractListingMailet
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class LeadCreatedMailet(
    val locationService: LocationService,
    val fileService: FileService,
    val messages: MessageSource,

    private val tenantService: TenantService,
    private val leadMessageService: LeadMessageService,
    private val userService: UserService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
) : AbstractListingMailet(locationService, fileService, messages) {
    companion object {
        const val SUBJECT_LISTING = "Listing #{{listingNumber}}: {{leadName}} vous a envoyé un message!"
        const val SUBJECT_AGENT = "{leadName}} vous a envoye un message!"
    }

    override fun service(event: Any): Boolean {
        if (event is LeadMessageReceivedEvent) {
            return onLeadCreated(event)
        }
        return false
    }

    private fun onLeadCreated(event: LeadMessageReceivedEvent): Boolean {
        val message = leadMessageService.get(event.messageId, event.tenantId)
        val lead = message.lead
        val listing = lead.listing
        val tenant = tenantService.get(event.tenantId)
        val users = userService.search(
            ids = listOf(lead.userId, lead.agentUserId),
            tenantId = event.tenantId,
            limit = 2,
        ).associateBy { user -> user.id }
        val recipient = users[lead.agentUserId]
        val user = users[lead.userId]

        val data = getData(lead, tenant, user, recipient)
        val body = when (listing) {
            null -> templateResolver.resolve("/listing/email/lead-created-agent.html", data)
            else -> templateResolver.resolve("/listing/email/lead-created-listing.html", data)
        }
        val subject = when (listing) {
            null -> SUBJECT_AGENT
            else -> SUBJECT_LISTING
                .replace("{{listingNumber}}", listing.listingNumber.toString())
                .replace("{{listingNumber}}", user?.displayName ?: "")
        }

        return recipient?.let {
            sender.send(
                recipient,
                subject = subject,
                body = body,
                attachments = emptyList(),
                tenantId = event.tenantId,
            )
        } ?: false
    }

    private fun getData(
        lead: LeadEntity,
        tenant: TenantEntity,
        user: UserEntity?,
        recipient: UserEntity?,
    ): Map<String, Any> {
        val map = mapOf(
            "leadName" to (user?.displayName ?: ""),
            "leadUrl" to "${tenant.portalUrl}/leads/${lead.id}",
        ) as Map<String, Any>
        return if (lead.listing == null) {
            map
        } else {
            map + getListingData(lead.listing, tenant, recipient)
        }
    }
}
