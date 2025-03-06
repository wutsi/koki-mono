package com.wutsi.koki.portal.email.mapper

import com.wutsi.koki.email.dto.Email
import com.wutsi.koki.email.dto.EmailSummary
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.portal.email.model.EmailModel
import com.wutsi.koki.portal.email.model.RecipientModel
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class EmailMapper : TenantAwareMapper() {
    fun toEmailModel(
        entity: Email,
        sender: UserModel?,
        files: Map<Long, FileModel>,
    ): EmailModel {
        val dateTimeFormat = createDateTimeFormat()
        val timeFormat = createTimeFormat()
        return EmailModel(
            id = entity.id,
            subject = entity.subject,
            body = entity.body,
            summary = entity.summary,
            sender = sender,
            createdAt = entity.createdAt,
            createdAtText = dateTimeFormat.format(entity.createdAt),
            createdAtMoment = formatMoment(entity.createdAt, dateTimeFormat, timeFormat),
            recipient = toRecipientModel(entity.recipient),
            attachmentFileCount = entity.attachmentFileIds.size,
            attachmentFiles = entity.attachmentFileIds.mapNotNull { id -> files[id] }
        )
    }

    fun toEmailModel(
        entity: EmailSummary,
        senders: Map<Long, UserModel>,
    ): EmailModel {
        val dateTimeFormat = createDateTimeFormat()
        val timeFormat = createTimeFormat()
        return EmailModel(
            id = entity.id,
            subject = entity.subject,
            summary = entity.summary,
            sender = senders[entity.senderId],
            createdAt = entity.createdAt,
            createdAtText = dateTimeFormat.format(entity.createdAt),
            createdAtMoment = formatMoment(entity.createdAt, dateTimeFormat, timeFormat),
            recipient = toRecipientModel(entity.recipient),
            attachmentFileCount = entity.attachmentCount,
        )
    }

    fun toRecipientModel(entity: Recipient): RecipientModel {
        return RecipientModel(
            type = entity.type,
            id = entity.id,
            name = entity.displayName ?: "",
            email = entity.email,
        )
    }
}
