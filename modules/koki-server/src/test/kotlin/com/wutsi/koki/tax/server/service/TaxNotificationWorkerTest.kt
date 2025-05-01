package com.wutsi.koki.tax.server.service

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.invoice.server.service.TenantTaxInitializer
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.event.TaxAssigneeChangedEvent
import com.wutsi.koki.tax.dto.event.TaxStatusChangedEvent
import com.wutsi.koki.tax.server.domain.TaxEntity
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.TypeEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.TypeService
import com.wutsi.koki.tenant.server.service.UserService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.context.support.ResourceBundleMessageSource
import kotlin.test.Test
import kotlin.test.assertEquals

class TaxNotificationWorkerTest {
    private val registry = mock<NotificationMQConsumer>()
    private val configurationService = mock<ConfigurationService>()
    private val taxService = mock<TaxService>()
    private val userService = mock<UserService>()
    private val accountService = mock<AccountService>()
    private val tenantService = mock<TenantService>()
    private val logger = DefaultKVLogger()
    private val emailService = mock<EmailService>()
    private val typeService = mock<TypeService>()
    private val fileService = mock<FileService>()
    private val worker = TaxNotificationWorker(
        registry = registry,
        configurationService = configurationService,
        taxService = taxService,
        userService = userService,
        tenantService = tenantService,
        emailService = emailService,
        messages = createMessageSource(),
        typeService = typeService,
        accountService = accountService,
        fileService = fileService,
        logger = logger,
    )

    private val tenant = TenantEntity(
        id = 555L,
        dateFormat = "dd/MM/yyyy",
        monetaryFormat = "C\$ #,###,##0.00",
        portalUrl = "https://localhost:8081"
    )
    private val user = UserEntity(
        id = 111L,
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        language = "ru",
    )
    private val account = AccountEntity(
        id = 777L,
        name = "Roger Milla",
        email = "roger.milla@gmail.com",
        language = "fr",
    )
    private val type = TypeEntity(
        id = 999L,
        name = "T1 - Personal Taxes",
    )
    private val tax = TaxEntity(
        id = 333L,
        tenantId = tenant.id!!,
        assigneeId = user.id,
        accountId = account.id!!,
        taxTypeId = type.id,
        fiscalYear = 2024,
        status = TaxStatus.REVIEWING,
    )
    private val files = listOf(
        FileEntity(id = 111L, language = "en"),
        FileEntity(id = 222L, language = "fr"),
        FileEntity(id = 333L, language = null),
    )

    val config = mapOf(
        ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT to "You have a new task",
        ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY to "Thank you!",

        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT to "The {{taxFiscalYear}} tax season is started",
        ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY to "Get ready for the tax season",

        ConfigurationName.TAX_EMAIL_DONE_ENABLED to "1",
        ConfigurationName.TAX_EMAIL_DONE_SUBJECT to "Your {{taxFiscalYear}} tax return are ready",
        ConfigurationName.TAX_EMAIL_DONE_BODY to "Thank you for your business"
    )
    private val configurations = config.entries.map { entry ->
        ConfigurationEntity(name = entry.key, value = entry.value)
    }

    @BeforeEach
    fun setUp() {
        doReturn(user).whenever(userService).get(any(), any())
        doReturn(account).whenever(accountService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(configurations).whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())
        doReturn(tax).whenever(taxService).get(any(), any())
        doReturn(type).whenever(typeService).get(any(), any())
        doReturn(files).whenever(fileService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun postConstruct() {
        worker.init()
        verify(registry).register(worker)
    }

    @Test
    fun preDestroy() {
        worker.destroy()
        verify(registry).unregister(worker)
    }

    @Test
    fun `assignee changed`() {
        val result = worker.notify(
            TaxAssigneeChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                assigneeId = user.id,
            )
        )

        assertEquals(true, result)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(tax.tenantId))

        assertEquals(user.email, request.firstValue.recipient.email)
        assertEquals(user.displayName, request.firstValue.recipient.displayName)
        assertEquals(null, request.firstValue.recipient.id)
        assertEquals(user.language, request.firstValue.recipient.language)
        assertEquals(ObjectType.UNKNOWN, request.firstValue.recipient.type)
        assertEquals(config[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY], request.firstValue.body)
        assertEquals(emptyList(), request.firstValue.attachmentFileIds)

        assertEquals(user.displayName, request.firstValue.data["recipientName"])
        assertEquals(tax.fiscalYear, request.firstValue.data["taxFiscalYear"])
        assertEquals(type.name, request.firstValue.data["taxType"])
        assertEquals("Reviewing", request.firstValue.data["taxStatus"])
        assertEquals("${tenant.portalUrl}/taxes/${tax.id}", request.firstValue.data["taxUrl"])
        assertEquals(account.name, request.firstValue.data["accountName"])
    }

    @Test
    fun `assignee changed - notification not enabled`() {
        doReturn(
            configurations.filter { config -> config.name != ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED }
        )
            .whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        val result = worker.notify(
            TaxAssigneeChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                assigneeId = user.id,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `assignee changed - assignee mismatch`() {
        doReturn(tax.copy(assigneeId = -1)).whenever(taxService).get(any(), any())

        val result = worker.notify(
            TaxAssigneeChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                assigneeId = user.id,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `assignee changed - unassigned`() {
        val result = worker.notify(
            TaxAssigneeChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                assigneeId = null,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `assignee changed - email`() {
        worker.notify(
            TaxAssigneeChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                assigneeId = user.id,
            )
        )

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            TaxNotificationWorker::class.java.getResourceAsStream(TenantTaxInitializer.EMAIL_ASSIGNEE_BODY_PATH),
            "utf-8"
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }

    @Test
    fun `tax gathering-documents`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.GATHERING_DOCUMENTS,
                formId = 11L
            )
        )

        assertEquals(true, result)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(tax.tenantId))

        assertEquals(account.email, request.firstValue.recipient.email)
        assertEquals(account.name, request.firstValue.recipient.displayName)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(account.language, request.firstValue.recipient.language)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(config[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY], request.firstValue.body)
        assertEquals(listOf(files[1].id), request.firstValue.attachmentFileIds)

        assertEquals(account.name, request.firstValue.data["recipientName"])
        assertEquals(tax.fiscalYear, request.firstValue.data["taxFiscalYear"])
        assertEquals(tenant.clientPortalUrl, request.firstValue.data["clientPortalUrl"])
    }

    @Test
    fun `tax gathering-documents without form`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.GATHERING_DOCUMENTS,
                formId = null
            )
        )

        assertEquals(true, result)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(tax.tenantId))

        assertEquals(account.email, request.firstValue.recipient.email)
        assertEquals(account.name, request.firstValue.recipient.displayName)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(account.language, request.firstValue.recipient.language)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(config[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY], request.firstValue.body)
        assertEquals(emptyList(), request.firstValue.attachmentFileIds)

        assertEquals(account.name, request.firstValue.data["recipientName"])
        assertEquals(tax.fiscalYear, request.firstValue.data["taxFiscalYear"])
        assertEquals(tenant.clientPortalUrl, request.firstValue.data["clientPortalUrl"])
    }

    @Test
    fun `tax gathering-documents - notification not enabled`() {
        doReturn(
            configurations.filter { config -> config.name != ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED }
        )
            .whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.GATHERING_DOCUMENTS,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `tax gathering-documents - email`() {
        worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.GATHERING_DOCUMENTS,
            )
        )

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            TaxNotificationWorker::class.java.getResourceAsStream(TenantTaxInitializer.EMAIL_GATHERING_DOCUMENTS_BODY_PATH),
            "utf-8"
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }

    @Test
    fun `tax done`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.DONE,
            )
        )

        assertEquals(true, result)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(tax.tenantId))

        assertEquals(account.email, request.firstValue.recipient.email)
        assertEquals(account.name, request.firstValue.recipient.displayName)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(account.language, request.firstValue.recipient.language)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(config[ConfigurationName.TAX_EMAIL_DONE_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.TAX_EMAIL_DONE_BODY], request.firstValue.body)
        assertEquals(emptyList(), request.firstValue.attachmentFileIds)

        assertEquals(account.name, request.firstValue.data["recipientName"])
        assertEquals(tax.fiscalYear, request.firstValue.data["taxFiscalYear"])
        assertEquals(tenant.clientPortalUrl, request.firstValue.data["clientPortalUrl"])
    }

    @Test
    fun `tax done - notification not enabled`() {
        doReturn(
            configurations.filter { config -> config.name != ConfigurationName.TAX_EMAIL_DONE_ENABLED }
        )
            .whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.DONE,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `tax done - email`() {
        worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.DONE,
            )
        )

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            TaxNotificationWorker::class.java.getResourceAsStream(TenantTaxInitializer.EMAIL_DONE_BODY_PATH),
            "utf-8"
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }

    @Test
    fun `tax new`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.NEW,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `tax reviewing`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.REVIEWING,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `tax unknown`() {
        val result = worker.notify(
            TaxStatusChangedEvent(
                taxId = tax.id!!,
                tenantId = tax.tenantId,
                status = TaxStatus.UNKNOWN,
            )
        )

        assertEquals(true, result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `event not supported`() {
        val result = worker.notify(FileUploadedEvent())

        assertEquals(false, result)
        verify(emailService, never()).send(any(), any())
    }

    fun createMessageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        return messageSource
    }
}
