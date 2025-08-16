package com.wutsi.koki.tenant.server.service.email

import com.wutsi.koki.email.server.service.AbstractEmailWorker
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.stereotype.Service

@Service
class SendUsernameEmailWorker(
    private val service: UserService,
    private val templateResolver: EmailTemplateResolver,
    private val sender: Sender,
) : AbstractEmailWorker() {
    companion object {
        const val SUBJECT = "Votre demande de nom d'utilisateur"
    }

    override fun notify(event: Any): Boolean {
        if (event is SendUsernameCommand) {
            send(event)
            return true
        }
        return false
    }

    private fun send(event: SendUsernameCommand) {
        val user = service.get(event.userId, event.tenantId)
        if (user.email.isNullOrEmpty()) {
            return
        }

        val data = mapOf(
            "username" to user.username
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
