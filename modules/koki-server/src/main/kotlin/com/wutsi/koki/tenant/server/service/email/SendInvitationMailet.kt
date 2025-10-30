package com.wutsi.koki.tenant.server.service.email

import com.wutsi.koki.email.server.mq.AbstractMailet
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.dto.event.InvitationCreatedEvent
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.InvitationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class SendInvitationMailet(
    private val invitationService: InvitationService,
    private val tenantService: TenantService,
    private val templateResolver: EmailTemplateResolver,
    private val templateEngine: TemplatingEngine,
    private val sender: Sender,
) : AbstractMailet() {
    companion object {
        const val SUBJECT = "Invitation Exclusive | Rejoignez {{platformName}}, le futur de l'Immobilier!"
    }

    override fun service(event: Any): Boolean {
        if (event is InvitationCreatedEvent) {
            send(event)
            return true
        }
        return false
    }

    private fun send(event: InvitationCreatedEvent) {
        try {
            val invitation = invitationService.get(event.invitationId, event.tenantId)
            if (invitation.status != InvitationStatus.PENDING) {
                return
            }

            val tenant = tenantService.get(event.tenantId)
            if (invitation.type == InvitationType.AGENT) {
                sendToAgent(invitation, tenant, event)
            }
        } catch (ex: NotFoundException) {
            // Invitation has been deleted
        }
    }

    private fun sendToAgent(invitation: InvitationEntity, tenant: TenantEntity, event: InvitationCreatedEvent) {
        val language = Locale("fr")
        val data = mapOf(
            "recipient" to invitation.displayName,
            "platformName" to tenant.name,
            "country" to Locale(language.language, tenant.country).getDisplayCountry(language),
            "signupUrl" to "${tenant.portalUrl}/signup?inv=${invitation.id}",
        )

        sender.send(
            recipient = Party(
                email = invitation.email,
                displayName = invitation.displayName,
            ),
            subject = templateEngine.apply(SUBJECT, data),
            body = templateResolver.resolve("/user/email/invitation-agent.html", data),
            attachments = emptyList(),
            tenantId = event.tenantId,
        )
    }
}
