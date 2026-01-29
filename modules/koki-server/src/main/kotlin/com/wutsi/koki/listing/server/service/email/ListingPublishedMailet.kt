package com.wutsi.koki.listing.server.service.email

import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class ListingPublishedMailet(
    val locationService: LocationService,
    val fileService: FileService,
    val messages: MessageSource,

    private val tenantService: TenantService,
    private val listingService: ListingService,
    private val userService: UserService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
    private val logger: KVLogger,
) : AbstractListingMailet(locationService, fileService, messages) {
    companion object {
        const val SUBJECT = "Votre listing a été publiée"
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
            logger.add("warning", "Listing is not active")
            return false
        }

        val tenant = tenantService.get(event.tenantId)
        val recipient = listing.sellerAgentUserId?.let { id -> userService.get(id, event.tenantId) } ?: return false
        val data = getListingData(listing, tenant, recipient)
        val body = templateResolver.resolve("/listing/email/published.html", data)
        val subject = SUBJECT

        sender.send(
            recipient = recipient,
            subject = subject,
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )
        return true
    }
}
