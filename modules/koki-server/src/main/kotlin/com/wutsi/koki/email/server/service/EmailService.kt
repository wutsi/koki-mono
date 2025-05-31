package com.wutsi.koki.email.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.dao.AttachmentRepository
import com.wutsi.koki.email.server.dao.EmailOwnerRepository
import com.wutsi.koki.email.server.dao.EmailRepository
import com.wutsi.koki.email.server.domain.AttachmentEntity
import com.wutsi.koki.email.server.domain.EmailEntity
import com.wutsi.koki.email.server.domain.EmailOwnerEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.MessagingNotConfiguredException
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.platform.messaging.smtp.SMTPType
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.platform.translation.TranslationService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.translation.server.service.TranslationServiceProvider
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.apache.tika.language.detect.LanguageDetector
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.UUID

@Service
class EmailService(
    private val dao: EmailRepository,
    private val attachmentDao: AttachmentRepository,
    private val ownerDao: EmailOwnerRepository,
    private val securityService: SecurityService,
    private val templatingEngine: TemplatingEngine,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val storageServiceBuilder: StorageServiceBuilder,
    private val businessService: BusinessService,
    private val fileService: FileService,
    private val filterSet: EmailFilterSet,
    private val languageDetector: LanguageDetector,
    private val translationServiceProvider: TranslationServiceProvider,
    private val em: EntityManager,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmailService::class.java)
    }

    fun get(id: String, tenantId: Long): EmailEntity {
        val account = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.EMAIL_NOT_FOUND)) }

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
        // AI api-key

        // Data
        val data = mutableMapOf<String, Any>()
        data.putAll(request.data)
        request.recipient.displayName?.let { name -> data["recipientName"] = name }

        // Translation
        val fromLanguage = detectLanguage(request)
        val toLanguage = request.recipient.language
        val translationService = getTranslationService(tenantId)

        // Email
        val id = UUID.randomUUID().toString()
        val subject = templatingEngine.apply(request.subject, data)
        val body = templatingEngine.apply(request.body, data)
        val xsubject = translate(subject, fromLanguage, toLanguage, translationService)
        val xbody = translate(body, fromLanguage, toLanguage, translationService)
        val email = EmailEntity(
            id = id,
            tenantId = tenantId,
            senderId = securityService.getCurrentUserIdOrNull(),
            recipientType = request.recipient.type,
            recipientId = request.recipient.id,
            recipientDisplayName = request.recipient.displayName,
            recipientEmail = request.recipient.email,
            subject = xsubject,
            body = xbody,
            summary = toSummary(xbody),
            attachmentCount = request.attachmentFileIds.size,
        )

        // Persist
        if (request.store) {
            dao.save(email)
            if (request.owner != null) {
                ownerDao.save(
                    EmailOwnerEntity(
                        emailId = email.id!!, ownerId = request.owner!!.id, ownerType = request.owner!!.type
                    )
                )
            }
        }

        // Attachments
        request.attachmentFileIds.forEach { fileId ->
            val att = AttachmentEntity(emailId = id, fileId = fileId)
            email.attachments.add(att)
            if (request.store) {
                attachmentDao.save(att)
            }
        }

        // Send
        val config = configurationService.search(
            names = SMTPMessagingServiceBuilder.CONFIG_NAMES, tenantId = tenantId
        ).map { cfg -> cfg.name to cfg.value }.toMap()
        try {
            val business = businessService.getOrNull(tenantId)
            val message = createMessage(request, email, config, business)
            try {
                logger.add("recipient_email", message.recipient.email)
                if (message.recipient.email.isEmpty()) {
                    throw ConflictException(
                        error = Error(code = ErrorCode.EMAIL_RECIPIENT_EMAIL_MISSING),
                    )
                }

                messagingServiceBuilder.build(config).send(message)
                logger.add("email_sent", true)
                logger.add("email_address", request.recipient.email)
                return email
            } finally {
                delete(message.attachments) // Delete all local files downloaded to free up diskspace
            }
        } catch (ex: MessagingNotConfiguredException) {
            throw ConflictException(
                error = Error(code = ErrorCode.EMAIL_SMTP_NOT_CONFIGURED),
                ex = ex,
            )
        } catch (ex: MessagingException) {
            throw ConflictException(
                error = Error(code = ErrorCode.EMAIL_DELIVERY_FAILED),
                ex = ex,
            )
        } catch (ex: Exception) {
            throw ConflictException(
                error = Error(code = ErrorCode.EMAIL_DELIVERY_FAILED),
                ex = ex,
            )
        }
    }

    private fun detectLanguage(request: SendEmailRequest): String {
        val text = request.subject + ".\n" + Jsoup.parse(request.body).text()
        return languageDetector.detect(text).language
    }

    private fun getTranslationService(tenantId: Long): TranslationService? {
        return try {
            translationServiceProvider.get(tenantId)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to get translation service", ex)
            null
        }
    }

    private fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String?,
        translationService: TranslationService?,
    ): String {
        if (fromLanguage == toLanguage || toLanguage == null || translationService == null) {
            return text
        }

        try {
            return translationService.translate(text, toLanguage).trimIndent()
        } catch (ex: Exception) {
            LOGGER.warn("Unable to translate: ${text.take(80)}...", ex)
            return text
        }
    }

    private fun createMessage(
        request: SendEmailRequest,
        email: EmailEntity,
        config: Map<String, String>,
        business: BusinessEntity?,
    ): Message {
        return Message(
            subject = email.subject,
            body = filterSet.filter(email.body, email.tenantId),
            mimeType = "text/html",
            recipient = Party(
                email = email.recipientEmail,
                displayName = email.recipientDisplayName
            ),
            attachments = request.attachmentFileIds.map { fileId ->
                val file = download(fileId, email.tenantId)
                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug("Adding attachment ${file.absolutePath}")
                }
                file
            },
            sender = Party(
                displayName = if (isSMTPKoki(config)) {
                    config[ConfigurationName.SMTP_FROM_PERSONAL] ?: business?.companyName
                } else {
                    ""
                },
            )
        )
    }

    private fun isSMTPKoki(config: Map<String, String>): Boolean {
        val type = config[ConfigurationName.SMTP_TYPE]
        return type == null || type.equals(SMTPType.KOKI.name, true)
    }

    private fun download(fileId: Long, tenantId: Long): File {
        val file = fileService.get(fileId, tenantId)
        val parent = File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString())
        parent.mkdirs()
        val localFile = File(parent, file.name)

        val configs = configurationService.search(
            keyword = "storage.",
            tenantId = tenantId
        ).map { cfg -> cfg.name to cfg.value }.toMap()
        val storageService = storageServiceBuilder.build(configs)

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Downloading File#$fileId -> ${localFile.absolutePath}")
        }
        val fout = FileOutputStream(localFile)
        fout.use {
            storageService.get(URI(file.url).toURL(), fout)
        }
        return localFile
    }

    private fun delete(files: List<File>) {
        files.forEach { file ->
            try {
                file.delete()
            } catch (ex: Exception) {
                LOGGER.warn("Unable to delete ${file.absolutePath}", ex)
            }
        }
    }

    private fun toSummary(html: String): String {
        val text = Jsoup.parse(html).text()
        return if (text.length > 255) {
            text.take(252) + "..."
        } else {
            text
        }
    }
}
