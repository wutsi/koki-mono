package com.wutsi.koki.email.server.mapper

import com.wutsi.koki.email.dto.Email
import com.wutsi.koki.email.dto.EmailSummary
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.server.domain.EmailEntity
import org.springframework.stereotype.Service

@Service
class EmailMapper {
    fun toEmail(entity: EmailEntity): Email {
        return Email(
            id = entity.id!!,
            subject = entity.subject,
            body = entity.body,
            summary = entity.summary,
            senderId = entity.senderId,
            recipient = Recipient(
                id = entity.recipientId,
                type = entity.recipientType,
            ),
            createdAt = entity.createdAt,
            attachmentFileIds = entity.attachments.map { attachment -> attachment.fileId }
        )
    }

    fun toEmailSummary(entity: EmailEntity): EmailSummary {
        return EmailSummary(
            id = entity.id!!,
            subject = entity.subject,
            summary = entity.summary,
            senderId = entity.senderId,
            recipient = Recipient(
                id = entity.recipientId,
                type = entity.recipientType,
            ),
            createdAt = entity.createdAt,
        )
    }
}
