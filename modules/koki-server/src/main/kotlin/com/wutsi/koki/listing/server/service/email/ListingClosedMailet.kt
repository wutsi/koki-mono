package com.wutsi.koki.listing.server.service.email

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
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
import java.text.DecimalFormat

@Service
class ListingClosedMailet(
    private val listingService: ListingService,
    private val userService: UserService,
    private val locationService: LocationService,
    private val tenantService: TenantService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
    private val logger: KVLogger,
) : AbstractMailet() {
    companion object {
        const val SUBJECT = "Clôture au {{address}} - Merci, {{recipient}}!"
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
            logger.add("success", false)
            logger.add("error", "No co-brokerage transaction")
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
        val fmt = DecimalFormat(tenant.monetaryFormat)

        val buyerAgent = userService.get(buyerAgentId, event.tenantId)
        val sellerAgent = userService.get(sellerAgentId!!, event.tenantId)
        val recipient = (buyerAgent.displayName ?: "")
        val data = mapOf(
            "recipient" to recipient,
            "address" to address,
            "listingNumber" to listing.listingNumber,
            "listingUrl" to "${tenant.portalUrl}/listings/${listing.id}",
            "sender" to sellerAgent.displayName,
            "senderPhotoUrl" to sellerAgent.photoUrl,
            "senderEmployer" to sellerAgent.employer,
            "senderMobile" to sellerAgent.mobile?.let { phone -> formatPhoneNumber(phone, sellerAgent.country) },
            "commission" to listing.finalBuyerAgentCommissionAmount?.let { amount -> fmt.format(amount) } +
                " (${listing.buyerAgentCommission}%)"
        ).filter { entry -> entry.value != null } as Map<String, Any>
        val body = templateResolver.resolve("/listing/email/closed-buyer.html", data)

        logger.add("recipient_user_id", buyerAgent.id)
        logger.add("recipient_user_email", buyerAgent.email)
        sender.send(
            recipient = buyerAgent,
            subject = SUBJECT.replace("{{address}}", address).replace("{{recipient}}", recipient),
            body = body,
            attachments = emptyList(),
            tenantId = event.tenantId,
        )

        logger.add("success", true)
        return true
    }

    protected fun formatPhoneNumber(number: String, country: String? = null): String {
        try {
            val pnu = PhoneNumberUtil.getInstance()
            val phoneNumber = pnu.parse(number, country ?: "")
            return pnu.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (ex: NumberParseException) {
            return number
        }
    }
}
