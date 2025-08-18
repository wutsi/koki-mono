package com.wutsi.koki.tenant.server.service.email

import com.wutsi.koki.email.server.service.AbstractEmailWorker
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.tenant.server.command.SendPasswordCommand
import com.wutsi.koki.tenant.server.service.PasswordResetTokenService
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.stereotype.Service

@Service
class SendPasswordEmailWorker(
    private val passwordResetService: PasswordResetTokenService,
    private val tenantService: TenantService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
) : AbstractEmailWorker() {
    companion object {
        const val SUBJECT = "Reinitialisation de votre mot de passe"
    }

    override fun notify(event: Any): Boolean {
        if (event is SendPasswordCommand) {
            send(event)
            return true
        }
        return false
    }

    private fun send(event: SendPasswordCommand) {
        val token = passwordResetService.get(event.tokenId, event.tenantId)
        val user = token.user
        if (user.email.isNullOrEmpty()) {
            return
        }

        val tenant = tenantService.get(event.tenantId)

        val data = mapOf(
            "recipient" to (user.displayName ?: ""),
            "resetPasswordUrl" to "${tenant.portalUrl}/forgot/password/reset?token=${token.id}"
        )
        val body = templateResolver.resolve("/user/email/password.html", data)

        sender.send(
            recipient = user,
            subject = SUBJECT,
            body = body,
            attachments = emptyList(),
            tenantId = user.tenantId,
        )
    }
}
