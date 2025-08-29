package com.wutsi.koki.tenant.server.service.email

import com.wutsi.koki.email.server.mq.AbstractMailet
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.stereotype.Service

@Service
class SendUsernameEmailMailet(
    private val userService: UserService,
    private val tenantService: TenantService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
) : AbstractMailet() {
    companion object {
        const val SUBJECT = "Votre demande de nom d'utilisateur"
    }

    override fun service(event: Any): Boolean {
        if (event is SendUsernameCommand) {
            send(event)
            return true
        }
        return false
    }

    private fun send(event: SendUsernameCommand) {
        val user = userService.get(event.userId, event.tenantId)
        if (user.email.isNullOrEmpty()) {
            return
        }

        val tenant = tenantService.get(event.tenantId)

        val data = mapOf(
            "username" to user.username,
            "recipient" to (user.displayName ?: ""),
            "loginUrl" to "${tenant.portalUrl}/login"
        )
        val body = templateResolver.resolve("/user/email/username.html", data)

        sender.send(
            recipient = user,
            subject = SUBJECT,
            body = body,
            attachments = emptyList(),
            tenantId = user.tenantId,
        )
    }
}
