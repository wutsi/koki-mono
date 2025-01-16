package com.wutsi.koki.portal.email.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.email.mapper.EmailMapper
import com.wutsi.koki.portal.email.model.EmailForm
import com.wutsi.koki.portal.email.model.EmailModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiEmails
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val koki: KokiEmails,
    private val mapper: EmailMapper,
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val userService: UserService,
) {
    fun email(id: String): EmailModel {
        val email = koki.email(id).email
        val sender = userService.user(email.senderId)
        val account = if (email.recipient.type == ObjectType.ACCOUNT) {
            accountService.account(id = email.recipient.id, fullGraph = false)
        } else {
            null
        }
        val contact = if (email.recipient.type == ObjectType.CONTACT) {
            contactService.contact(id = email.recipient.id)
        } else {
            null
        }
        return mapper.toEmailModel(
            entity = email,
            sender = sender,
            account = account,
            contact = contact
        )
    }

    fun emails(
        ids: List<String> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<EmailModel> {
        val emails = koki.emails(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
        ).emails

        // Senders
        val senderIds = emails.map { email -> email.senderId }
            .toSet()
        val senders = if (senderIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = senderIds.toList(),
                limit = senderIds.size,
            ).associateBy { user -> user.id }
        }

        // Accounts
        val accountIds = emails
            .filter { email -> email.recipient.type == ObjectType.ACCOUNT }
            .map { email -> email.recipient.id }
            .toSet()
        val accounts = if (accountIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            accountService.accounts(
                ids = accountIds.toList(),
                limit = accountIds.size,
            ).associateBy { account -> account.id }
        }

        // Contacts
        val contactIds = emails
            .filter { email -> email.recipient.type == ObjectType.CONTACT }
            .map { email -> email.recipient.id }
            .toSet()
        val contacts = if (contactIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            contactService.contacts(
                ids = contactIds.toList(),
                limit = contactIds.size,
            ).associateBy { contact -> contact.id }
        }

        return emails.map { email ->
            mapper.toEmailModel(
                entity = email,
                senders = senders,
                accounts = accounts,
                contacts = contacts,
            )
        }
    }

    fun send(form: EmailForm): String {
        return koki.send(
            request = SendEmailRequest(
                subject = form.subject,
                body = form.body,
                recipient = Recipient(
                    type = form.recipientType ?: ObjectType.UNKNOWN,
                    id = if (form.recipientType == ObjectType.ACCOUNT) {
                        form.accountId ?: -1L
                    } else if (form.recipientType == ObjectType.CONTACT) {
                        form.contactId ?: -1L
                    } else {
                        -1L
                    }
                ),
                owner = if (form.ownerId == null || form.ownerType == null) {
                    null
                } else {
                    ObjectReference(form.ownerId, form.ownerType)
                },
            )
        ).emailId
    }
}
