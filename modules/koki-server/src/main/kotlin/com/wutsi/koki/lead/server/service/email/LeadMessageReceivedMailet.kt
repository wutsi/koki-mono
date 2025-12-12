package com.wutsi.koki.lead.server.service.email

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.email.AbstractListingMailet
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class LeadMessageReceivedMailet(
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
        const val SUBJECT_LISTING_0 = "NOUVEAU PROSPECT: Demande d'information pour {{address}} ({{leadName}})"
        const val SUBJECT_LISTING_N = "\uD83D\uDD25 PROSPECT ENGAGÉ: {{n}}ème Message pour {{address}} ({{leadName}})"

        const val SUBJECT_AGENT_0 = "NOUVEAU PROSPECT : Demande de contact ({{leadName}})"
        const val SUBJECT_AGENT_N = "\uD83D\uDD25 PROSPECT ENGAGÉ : {{n}}ème Message ({{leadName}})"
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
        val to = users[lead.agentUserId]!!
        val from = users[lead.userId]!!

        val data = getData(event, message, tenant, from, to)
        return sender.send(
            to,
            subject = getSubject(event, message, data),
            body = getBody(listing, data),
            attachments = emptyList(),
            tenantId = event.tenantId,
        )
    }

    private fun getSubject(
        event: LeadMessageReceivedEvent,
        message: LeadMessageEntity,
        data: Map<String, Any>
    ): String {
        val leadName = data["leadName"]?.toString() ?: ""
        return when (message.lead.listing) {
            null -> if (event.newLead) {
                SUBJECT_AGENT_0.replace("{{leadName}}", leadName)
            } else {
                val rank = leadMessageService.getMessageRank(message)
                SUBJECT_AGENT_N.replace("{{leadName}}", leadName)
                    .replace("{{n}}", rank.toString())
            }

            else -> {
                val address = data["listingAddress"]?.toString() ?: ""
                val subject = if (event.newLead) {
                    SUBJECT_LISTING_0.replace("{{address}}", address)
                } else {
                    val rank = leadMessageService.getMessageRank(message)
                    SUBJECT_LISTING_N
                        .replace("{{n}}", rank.toString())
                }
                subject.replace("{{address}}", address)
                    .replace("{{leadName}}", leadName)
            }
        }
    }

    private fun getBody(listing: ListingEntity?, data: Map<String, Any>): String {
        return when (listing) {
            null -> templateResolver.resolve("/listing/email/lead-created-agent.html", data)
            else -> templateResolver.resolve("/listing/email/lead-created-listing.html", data)
        }
    }

    private fun getData(
        event: LeadMessageReceivedEvent,
        message: LeadMessageEntity,
        tenant: TenantEntity,
        from: UserEntity,
        to: UserEntity,
    ): Map<String, Any> {
        val lead = message.lead
        val fmt = SimpleDateFormat(tenant.dateTimeFormat)
        val map = mapOf(
            "leadName" to from.displayName,
            "leadUrl" to "${tenant.portalUrl}/leads/${lead.id}",
            "leadEmail" to from.email,
            "leadPhoneNumber" to from.mobile?.let { number -> formatPhoneNumber(number, from.country) },
            "leadPhoneNumberUrl" to from.mobile?.let { number -> toPhoneUrl(number) },
            "leadMessageDate" to fmt.format(message.createdAt),
            "leadMessage" to message.content,
            "leadNew" to event.newLead,
        ).filter { entry -> entry.value != null } as Map<String, Any>
        return if (lead.listing == null) {
            map
        } else {
            map + getListingData(lead.listing, tenant, to)
        }
    }

    private fun toPhoneUrl(number: String): String {
        val xnumber = if (number.startsWith("+")) number.substring(1) else number
        return "tel:$xnumber"
    }

    private fun formatPhoneNumber(number: String, country: String? = null): String {
        try {
            val pnu = PhoneNumberUtil.getInstance()
            val phoneNumber = pnu.parse(number, country ?: "")
            return pnu.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (ex: NumberParseException) {
            return number
        }
    }
}
