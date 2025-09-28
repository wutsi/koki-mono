package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.email.server.mq.AbstractMailet
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
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
        if (listing.status != ListingStatus.ACTIVE) {
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
        ).filterNotNull().joinToString(", ")

        val tenant = tenantService.get(event.tenantId)

        val data = mapOf(
            "recipient" to (agent.displayName ?: ""),
            "address" to address,
            "listingNumber" to listing.listingNumber,
            "listingUrl" to "${tenant.portalUrl}/listings/${listing.id}",
        )
        val body = templateResolver.resolve("/listing/email/published.html", data)

        logger.add("recipient_user_id", agent.id)
        logger.add("recipient_user_email", agent.email)
        sender.send(
            recipient = agent,
            subject = SUBJECT,
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )

        logger.add("success", true)
        return true
    }
}
