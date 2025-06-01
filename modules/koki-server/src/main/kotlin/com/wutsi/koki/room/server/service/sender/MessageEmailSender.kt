package com.wutsi.koki.room.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class MessageEmailSender(
    private val messageService: MessageService,
    private val roomService: RoomService,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val tenantService: TenantService,
    private val fileService: FileService,
) {
    companion object {
        const val SUBJECT = "You have a new message about your property"
    }

    fun send(event: MessageSentEvent) {
        if (event.owner?.type != ObjectType.ROOM) {
            return
        }

        val room = roomService.get(event.owner?.id ?: -1L, event.tenantId)
        val message = messageService.get(event.messageId, event.tenantId)
        val account = accountService.get(room.accountId, event.tenantId)
        val tenant = tenantService.get(event.tenantId)
        val heroImage = room.heroImageId?.let { id -> fileService.get(id, event.tenantId) }

        emailService.send(
            tenantId = event.tenantId,
            request = SendEmailRequest(
                recipient = Recipient(
                    id = account.id,
                    type = ObjectType.ACCOUNT,
                    email = account.email,
                    displayName = account.name,
                    language = account.language,
                ),
                subject = SUBJECT,
                body = IOUtils.toString(this::class.java.getResourceAsStream("/room/email/message.html"), "utf-8"),
                data = mapOf(
                    "senderName" to message.senderName,
                    "body" to message.body,
                    "roomUrl" to "${tenant.portalUrl}/rooms/${room.id}",
                    "roomTitle" to room.title,
                    "messageUrl" to "${tenant.portalUrl}/rooms/${room.id}?tab=message",
                    "heroImageUrl" to heroImage?.url,
                ).filter { entry -> entry.value != null } as Map<String, Any>,
                store = false,
            )
        )
    }
}
