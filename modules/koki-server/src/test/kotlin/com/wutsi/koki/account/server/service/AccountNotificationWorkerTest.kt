package com.wutsi.koki.account.server.service

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.dto.event.InvitationCreatedEvent
import com.wutsi.koki.account.server.domain.InvitationEntity
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountNotificationWorkerTest {
    private val registry = mock<NotificationMQConsumer>()

    private val invitationService = mock<InvitationService>()
    private val accountService = mock<AccountService>()
    private val configurationService = mock<ConfigurationService>()
    private val businessService = mock<BusinessService>()
    private val tenantService = mock<TenantService>()
    private val emailService = mock<EmailService>()
    private val logger = mock<KVLogger>()

    private val worker = AccountNotificationWorker(
        registry = registry,
        invitationService = invitationService,
        accountService = accountService,
        configurationService = configurationService,
        businessService = businessService,
        tenantService = tenantService,
        emailService = emailService,
        logger = DefaultKVLogger(),
    )

    val tenant = TenantEntity(
        id = 11L,
        clientPortalUrl = "https//portal.woo-llc.com"
    )
    val business = BusinessEntity(
        id = 22L,
        companyName = "WOO LLC"
    )
    val account = AccountEntity(
        id = 33L,
        tenantId = tenant.id!!,
        name = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        accountUserId = null,
        language = "fr",
    )
    val invitation = InvitationEntity(
        id = "4444",
        tenantId = tenant.id!!,
        accountId = account.id!!,
    )

    val configs = mapOf(
        ConfigurationName.ACCOUNT_INVITATION_EMAIL_SUBJECT to "Welcome to {{businessName}} cloud",
        ConfigurationName.ACCOUNT_INVITATION_EMAIL_BODY to "Hello workd"
    )

    val invitationEvent = InvitationCreatedEvent(
        invitationId = invitation.id!!,
        tenantId = invitation.tenantId,
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(business).whenever(businessService).get(any())
        doReturn(account).whenever(accountService).get(any(), any())
        doReturn(invitation).whenever(invitationService).get(any(), any())
        doReturn(
            configs.map { cfg -> ConfigurationEntity(name = cfg.key, value = cfg.value) }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun init() {
        worker.init()
        verify(registry).register(worker)
    }

    @Test
    fun destroy() {
        worker.destroy()
        verify(registry).unregister(worker)
    }

    @Test
    fun `invitation created`() {
        val result = worker.notify(invitationEvent)

        assertTrue(result)

        val req = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(req.capture(), eq(invitation.tenantId))

        assertEquals(account.language, req.firstValue.recipient.language)
        assertEquals(account.name, req.firstValue.recipient.displayName)
        assertEquals(account.email, req.firstValue.recipient.email)
        assertEquals(account.id, req.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, req.firstValue.recipient.type)

        assertEquals(account.id, req.firstValue.owner?.id)
        assertEquals(ObjectType.ACCOUNT, req.firstValue.owner?.type)

        assertEquals(configs[ConfigurationName.ACCOUNT_INVITATION_EMAIL_SUBJECT], req.firstValue.subject)
        assertEquals(configs[ConfigurationName.ACCOUNT_INVITATION_EMAIL_BODY], req.firstValue.body)

        assertEquals(3, req.firstValue.data.size)
        assertEquals(business.companyName, req.firstValue.data["businessName"])
        assertEquals("${tenant.clientPortalUrl}/invitations/${invitation.id}", req.firstValue.data["invitationUrl"])
        assertEquals(account.name, req.firstValue.data["recipientName"])

        assertEquals(0, req.firstValue.attachmentFileIds.size)
    }

    @Test
    fun `invitation created - no config`() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        val result = worker.notify(invitationEvent)

        assertTrue(result)
        val req = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(req.capture(), eq(invitation.tenantId))

        assertEquals(
            TenantAccountInitializer.INVITATION_EMAIL_SUBJECT,
            req.firstValue.subject
        )
        assertEquals(
            IOUtils.toString(
                this::class.java.getResourceAsStream(TenantAccountInitializer.INVITATION_EMAIL_BODY_PATH),
                "utf-8"
            ),
            req.firstValue.body
        )
    }

    @Test
    fun `invitation created - no email`() {
        doReturn(account.copy(email = "")).whenever(accountService).get(any(), any())

        val result = worker.notify(invitationEvent)

        assertTrue(result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `invitation created - account as user`() {
        doReturn(account.copy(accountUserId = 333L)).whenever(accountService).get(any(), any())

        val result = worker.notify(invitationEvent)

        assertTrue(result)
        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `invitation email`() {
        worker.notify(invitationEvent)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            this::class.java.getResourceAsStream(TenantAccountInitializer.INVITATION_EMAIL_BODY_PATH),
            "utf-8",
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }

    @Test
    fun `event not supported`() {
        val result = worker.notify(FileUploadedEvent())

        assertFalse(result)
        verify(emailService, never()).send(any(), any())
    }
}
