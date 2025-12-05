package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class ListingClosedMailet(
    val locationService: LocationService,
    val fileService: FileService,
    val messages: MessageSource,

    private val listingService: ListingService,
    private val userService: UserService,
    private val tenantService: TenantService,
    private val agentService: AgentService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
    private val logger: KVLogger,
) : AbstractListingMailet(locationService, fileService, messages) {
    companion object {
        const val SUBJECT_SOLD = "Listing #{{listingNumber}}: La propriété est VENDU!"
        const val SUBJECT_RENTED = "Listing #{{listingNumber}}: La propriété est LOUÉE!"
    }

    override fun service(event: Any): Boolean {
        if (event is ListingStatusChangedEvent && (event.status == ListingStatus.SOLD || event.status == ListingStatus.RENTED)) {
            return service(event)
        } else {
            return false
        }
    }

    private fun service(event: ListingStatusChangedEvent): Boolean {
        val listing = listingService.get(event.listingId, event.tenantId)

        val buyerAgentId = listing.buyerAgentUserId
        val sellerAgentId = listing.sellerAgentUserId
        if (buyerAgentId == null || buyerAgentId == sellerAgentId) {
            logger.add("warning", "Not co-brokerage transaction - buyer and seller agent are the same!")
            return false
        }

        val tenant = tenantService.get(event.tenantId)

        val buyerAgent = userService.get(buyerAgentId, event.tenantId)
        val sellerAgent = userService.get(sellerAgentId!!, event.tenantId)
        val data = getData(listing, tenant, buyerAgent, sellerAgent)
        val body = templateResolver.resolve("/listing/email/closed-buyer.html", data)
        val subject = getSubject(event).replace("{{listingNumber}}", listing.listingNumber.toString())

        return sender.send(
            recipient = buyerAgent,
            subject = subject,
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )
    }

    private fun getSubject(event: ListingStatusChangedEvent): String {
        return if (event.status == ListingStatus.SOLD) {
            SUBJECT_SOLD
        } else {
            SUBJECT_RENTED
        }
    }

    private fun getData(
        listing: ListingEntity,
        tenant: TenantEntity,
        buyer: UserEntity,
        seller: UserEntity
    ): Map<String, Any> {
        val agent = agentService.getByUser(seller.id!!, seller.tenantId)
        val agentName = listOfNotNull(
            seller.displayName,
            seller.employer?.let { employer -> "($employer)" }
        ).joinToString(separator = " ")
        val data = mapOf(
            "agentName" to agentName,
            "agentUrl" to "${tenant.portalUrl}/agents/${agent.id}",
        ).filter { entry -> entry.value != null } as Map<String, Any>

        return data + getListingData(listing, tenant, buyer)
    }
}
