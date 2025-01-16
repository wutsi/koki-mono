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
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.util.Date

@Service
class EmailMapper(
    private val messages: MessageSource,
) : TenantAwareMapper() {
    fun toEmailModel(
        entity: Email,
        sender: UserModel,
        account: AccountModel?,
        contact: ContactModel?,
    ): EmailModel {
        val dateTimeFormat = createDateTimeFormat()
        val timeFormat = createDateTime()
        return EmailModel(
            id = entity.id,
            subject = entity.subject,
            body = entity.body,
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
        val timeFormat = createDateTime()
        return EmailModel(
            id = entity.id,
            subject = entity.subject,
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

    fun formatMoment(date: Date, dateTimeFormat: DateFormat, timeFormat: DateFormat): String {
        val days = (System.currentTimeMillis() - date.time) / (1000 * 60 * 60 * 24)
        val locale = LocaleContextHolder.getLocale()
        return if (days < 1) {
            messages.getMessage("moment.today", emptyArray(), locale) + " - " + timeFormat.format(date)
        } else if (days < 2) {
            messages.getMessage("moment.yesterday", emptyArray(), locale) + " - " + timeFormat.format(date)
        } else {
            dateTimeFormat.format(date)
        }
    }
}
