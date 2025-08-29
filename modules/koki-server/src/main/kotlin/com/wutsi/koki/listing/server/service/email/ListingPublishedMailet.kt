package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.email.server.mq.AbstractMailet
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.stereotype.Service

@Service
class ListingPublishedMailet(
    private val listingService: ListingService,
    private val userService: UserService,
    private val locationService: LocationService,
    private val tenantService: TenantService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
    private val logger: KVLogger,
) : AbstractMailet() {
    companion object {
        const val SUBJECT = "Votre propriété a été publiée"
    }

    override fun service(event: Any): Boolean {
        if (event is ListingStatusChangedEvent && event.status == ListingStatus.ACTIVE) {
            return service(event)
        } else {
            return false
        }
    }

    private fun service(event: ListingStatusChangedEvent): Boolean {
        val listing = listingService.get(event.listingId, event.tenantId)
        if (listing.sellerEmail.isNullOrEmpty()) {
            logger.add("success", false)
            logger.add("error", "Seller has no email")
            return false
        } else if (listing.status != ListingStatus.ACTIVE) {
            logger.add("success", false)
            logger.add("error", "Listing is not active")
            return false
        }

        val agent = listing.sellerAgentUserId?.let { id -> userService.get(id, event.tenantId) }
        if (agent == null) {
            logger.add("success", false)
            logger.add("error", "No agent associate with listing")
            return false
        }

        val city = listing.cityId?.let { id -> locationService.get(id) }
        val neighbourhood = listing.neighbourhoodId?.let { id -> locationService.get(id) }
        val address = listOf(
            listing.street,
            neighbourhood?.name,
            city?.name
        ).filterNotNull().joinToString(",")

        val tenant = tenantService.get(event.tenantId)

        val data = mapOf(
            "recipient" to listing.sellerName,
            "address" to address,
            "listingNumber" to listing.listingNumber,
            "rental" to (listing.listingType == ListingType.RENTAL),
            "listingUrl" to "${tenant.clientPortalUrl}/l/${listing.id}",
            "agentDisplayName" to agent.displayName,
            "agentEmployer" to agent.employer,
            "agentEmail" to agent.email,
            "agentMobile" to agent.mobile,
            "agentPhotoUrl" to agent.photoUrl,
        ).filter { entry -> entry.value != null } as Map<String, Any>
        val body = templateResolver.resolve("/listing/email/published.html", data)
        sender.send(
            recipient = Party(
                displayName = listing.sellerName,
                email = listing.sellerEmail!!,
            ),
            subject = SUBJECT,
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )

        logger.add("success", true)
        return true
    }
}
