package com.wutsi.koki.portal.email.mapper

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Email
import com.wutsi.koki.email.dto.EmailSummary
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.email.model.EmailModel
import com.wutsi.koki.portal.email.model.RecipientModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class EmailMapper : TenantAwareMapper() {
    fun toEmailModel(
        entity: Email,
        sender: UserModel,
        account: AccountModel?,
        contact: ContactModel?,
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
            recipient = toRecipientModel(entity.recipient, account, contact),
        )
    }

    fun toEmailModel(
        entity: EmailSummary,
        senders: Map<Long, UserModel>,
        accounts: Map<Long, AccountModel>,
        contacts: Map<Long, ContactModel>,
    ): EmailModel {
        val dateTimeFormat = createDateTimeFormat()
        val timeFormat = createTimeFormat()
        return EmailModel(
            id = entity.id,
            subject = entity.subject,
            summary = entity.summary,
            sender = senders[entity.senderId] ?: UserModel(),
            createdAt = entity.createdAt,
            createdAtText = dateTimeFormat.format(entity.createdAt),
            createdAtMoment = formatMoment(entity.createdAt, dateTimeFormat, timeFormat),
            recipient = toRecipientModel(
                entity.recipient,
                accounts[entity.recipient.id],
                contacts[entity.recipient.id]
            ),
        )
    }

    fun toRecipientModel(entity: Recipient, account: AccountModel?, contact: ContactModel?): RecipientModel {
        return RecipientModel(
            type = entity.type,
            id = entity.id,
            name = when (entity.type) {
                ObjectType.CONTACT -> (contact?.name ?: "")
                ObjectType.ACCOUNT -> (account?.name ?: "")
                else -> ""
            },
            email = when (entity.type) {
                ObjectType.CONTACT -> contact?.email
                ObjectType.ACCOUNT -> account?.email
                else -> ""
            },
        )
    }
}
