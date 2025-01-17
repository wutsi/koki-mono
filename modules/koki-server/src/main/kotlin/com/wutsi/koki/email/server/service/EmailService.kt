package com.wutsi.koki.email.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.contact.server.service.ContactService
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.dao.EmailOwnerRepository
import com.wutsi.koki.email.server.dao.EmailRepository
import com.wutsi.koki.email.server.domain.EmailEntity
import com.wutsi.koki.email.server.domain.EmailOwnerEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.MessagingType
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmailService(
    private val dao: EmailRepository,
    private val ownerDao: EmailOwnerRepository,
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val securityService: SecurityService,
    private val templatingEngine: TemplatingEngine,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val filterSet: EmailFilterSet,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): EmailEntity {
        val account = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.EMAIL_NOT_FOUND)) }

        if (account.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.EMAIL_NOT_FOUND))
        }
        return account
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<EmailEntity> {
        val jql = StringBuilder("SELECT E FROM EmailEntity AS E")
        if (ownerId != null || ownerType != null) {
            jql.append(" JOIN E.emailOwners AS O")
        }

        jql.append(" WHERE E.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND E.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND O.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND O.ownerType = :ownerType")
        }
        jql.append(" ORDER BY E.createdAt DESC")

        val query = em.createQuery(jql.toString(), EmailEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun send(request: SendEmailRequest, tenantId: Long): EmailEntity {
        // Save email
        val email = dao.save(
            EmailEntity(
                tenantId = tenantId,
                id = UUID.randomUUID().toString(),
                recipientId = request.recipient.id,
                recipientType = request.recipient.type,
                subject = request.subject,
                body = request.body,
                senderId = securityService.getCurrentUserId(),
            )
        )

        // Reference
        if (request.owner != null) {
            ownerDao.save(
                EmailOwnerEntity(
                    emailId = email.id!!,
                    ownerId = request.owner!!.id,
                    ownerType = request.owner!!.type
                )
            )
        }

        // Send email
        try {
            val messagingService = createMessagingService(tenantId)
            val message = createMessage(request, email)
            messagingService.send(message)
            return email
        } catch (ex: MessagingException) {
            throw ConflictException(
                error = Error(code = ErrorCode.EMAIL_DELIVERY_FAILED),
                ex = ex
            )
        }
    }

    private fun createMessage(request: SendEmailRequest, email: EmailEntity): Message {
        val data = mutableMapOf<String, Any>()
        data.putAll(request.data)

        val recipient = toParty(email)
        recipient.displayName?.let { name -> data["recipient_name"] = name }

        val body = templatingEngine.apply(email.body, data)

        return Message(
            subject = email.subject,
            body = filterSet.filter(body, email.tenantId),
            mimeType = "text/html",
            recipient = recipient
        )
    }

    private fun toParty(email: EmailEntity): Party {
        if (email.recipientType == ObjectType.ACCOUNT) {
            val account = accountService.get(email.recipientId, email.tenantId)
            return toParty(account.name, account.email)
        } else if (email.recipientType == ObjectType.CONTACT) {
            val contact = contactService.get(email.recipientId, email.tenantId)
            return toParty("${contact.firstName} ${contact.lastName}".trim(), contact.email)
        } else {
            throw ConflictException(
                error = Error(ErrorCode.EMAIL_RECIPIENT_NOT_SUPPORTED)
            )
        }
    }

    private fun toParty(displayName: String, email: String?): Party {
        if (email.isNullOrEmpty()) {
            throw ConflictException(
                error = Error(ErrorCode.EMAIL_RECIPIENT_EMAIL_MISSING)
            )
        }
        return Party(displayName = displayName, email = email)
    }

    private fun createMessagingService(tenantId: Long): MessagingService {
        val config = configurationService.search(
            names = SMTPMessagingServiceBuilder.CONFIG_NAMES,
            tenantId = tenantId
        ).map { cfg -> cfg.name to cfg.value }
            .toMap()
        return messagingServiceBuilder.build(MessagingType.EMAIL, config)
    }
}
