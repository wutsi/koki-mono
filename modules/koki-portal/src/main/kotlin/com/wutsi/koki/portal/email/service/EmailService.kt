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
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiEmails
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val koki: KokiEmails,
    private val mapper: EmailMapper,
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val fileService: FileService,
    private val userService: UserService,
) {
    fun email(id: String): EmailModel {
        val email = koki.email(id).email
        val sender = email.senderId?.let { id -> userService.user(id) }
        val files = if (email.attachmentFileIds.isNotEmpty()) {
            fileService.files(
                ids = email.attachmentFileIds,
                limit = email.attachmentFileIds.size,
            ).associateBy { file -> file.id }
        } else {
            emptyMap()
        }
        return mapper.toEmailModel(entity = email, sender = sender, files = files)
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
            ids = ids, ownerId = ownerId, ownerType = ownerType, limit = limit, offset = offset
        ).emails

        // Senders
        val senderIds = emails.map { email -> email.senderId }.filterNotNull().toSet()
        val senders = if (senderIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = senderIds.toList(),
                limit = senderIds.size,
            ).associateBy { user -> user.id }
        }

        return emails.map { email ->
            mapper.toEmailModel(
                entity = email,
                senders = senders,
            )
        }
    }

    fun send(form: EmailForm): String {
        val account = if (form.recipientType == ObjectType.ACCOUNT && form.accountId != null) {
            accountService.account(form.accountId, fullGraph = false)
        } else {
            null
        }
        val contact = if (form.recipientType == ObjectType.CONTACT && form.contactId != null) {
            contactService.contact(form.contactId, fullGraph = false)
        } else {
            null
        }

        return koki.send(
            request = SendEmailRequest(
                subject = form.subject,
                body = form.body,
                recipient = Recipient(
                    type = form.recipientType ?: ObjectType.UNKNOWN,
                    id = when (form.recipientType) {
                        ObjectType.ACCOUNT -> form.accountId
                        ObjectType.CONTACT -> form.contactId
                        else -> null
                    },
                    displayName = when (form.recipientType) {
                        ObjectType.ACCOUNT -> account?.name
                        ObjectType.CONTACT -> "${contact?.firstName} ${contact?.lastName}"
                        else -> null
                    },
                    email = when (form.recipientType) {
                        ObjectType.ACCOUNT -> account?.email ?: ""
                        ObjectType.CONTACT -> contact?.email ?: ""
                        else -> ""
                    },
                    language = when (form.recipientType) {
                        ObjectType.ACCOUNT -> account?.language
                        ObjectType.CONTACT -> contact?.language
                        else -> null
                    },
                ),
                owner = if (form.ownerId == null || form.ownerType == null) {
                    null
                } else {
                    ObjectReference(form.ownerId, form.ownerType)
                },
                attachmentFileIds = form.attachmentFileId,
            )
        ).emailId
    }
}
