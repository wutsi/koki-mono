package com.wutsi.koki.workflow.server.engine

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.event.ActivityDoneEvent
import com.wutsi.koki.workflow.dto.event.ActivityStartedEvent
import com.wutsi.koki.workflow.dto.event.ExternalEvent
import com.wutsi.koki.workflow.dto.event.WorkflowDoneEvent
import com.wutsi.koki.workflow.dto.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.WorkflowService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NotificationEventListenerTest {
    private val workflowService = mock<WorkflowService>()
    private val activityService = mock<ActivityService>()
    private val activityInstanceService = mock<ActivityInstanceService>()
    private val templatingEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    private val configurationService = mock<ConfigurationService>()
    private val messagingService = mock<MessagingService>()
    private val userService = mock<UserService>()
    private val tenantService = mock<TenantService>()
    private val messagingServiceBuilder = mock<MessagingServiceBuilder>()
    private val logger: KVLogger = DefaultKVLogger()
    private val listener = NotificationEventListener(
        workflowService = workflowService,
        activityService = activityService,
        activityInstanceService = activityInstanceService,
        templateEngine = templatingEngine,
        messagingServiceBuilder = messagingServiceBuilder,
        configurationService = configurationService,
        userService = userService,
        tenantService = tenantService,
        logger = logger,
    )

    private val tenant = TenantEntity(id = 999, portalUrl = "http://localhost:8081")
    private val user = UserEntity(id = 777, displayName = "Ray Sponsible", email = "ray.sponsible@gmail.com")
    private val workflow = WorkflowEntity(id = 111, name = "WKF-001", title = "Incident Management")
    private val activity =
        ActivityEntity(id = 333, workflowId = workflow.id!!, type = ActivityType.USER, title = "Report")
    private val activityInstance = ActivityInstanceEntity(
        id = "555",
        activityId = activity.id!!,
        assigneeId = user.id!!
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(user).whenever(userService).get(any(), any())

        doReturn(workflow).whenever(workflowService).get(any<Long>(), any())
        doReturn(activity).whenever(activityService).get(any())
        doReturn(activityInstance).whenever(activityInstanceService).get(any(), any())

        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())
        doReturn(messagingService).whenever(messagingServiceBuilder).build(any(), any())
    }

    @AfterTest
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onActivityStarted() {
        val event = ActivityStartedEvent(
            tenantId = tenant.id!!,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertTrue(result)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())

        assertEquals(user.email, msg.firstValue.recipient.email)
        assertEquals(user.displayName, msg.firstValue.recipient.displayName)
        assertTrue(msg.firstValue.body.contains("${tenant.portalUrl}/tasks/${activityInstance.id}"))
    }

    @Test
    fun `onActivityStarted - MANUAL`() {
        doReturn(activity.copy(type = ActivityType.MANUAL)).whenever(activityService).get(any())

        val event = ActivityStartedEvent(
            tenantId = tenant.id!!,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertTrue(result)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())

        assertEquals(user.email, msg.firstValue.recipient.email)
        assertEquals(user.displayName, msg.firstValue.recipient.displayName)
        assertTrue(msg.firstValue.body.contains("${tenant.portalUrl}/tasks/${activityInstance.id}/complete"))
    }

    @Test
    fun `onActivityStarted - SERVICE`() {
        doReturn(activity.copy(type = ActivityType.SERVICE)).whenever(activityService).get(any())

        val event = ActivityStartedEvent(
            tenantId = tenant.id!!,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertFalse(result)
        verify(messagingService, never()).send(any())
    }

    @Test
    fun `onActivityStarted - no recipient`() {
        doReturn(activityInstance.copy(assigneeId = null)).whenever(activityInstanceService).get(any(), any())

        val event = ActivityStartedEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertFalse(result)
        verify(messagingService, never()).send(any())
    }

    @Test
    fun onWorkflowStarted() {
        val event = WorkflowStartedEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
        )
        val result = listener.handle(event)
        assertFalse(result)
    }

    @Test
    fun onWorkflowDone() {
        val event = WorkflowDoneEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
        )
        val result = listener.handle(event)
        assertFalse(result)
    }

    @Test
    fun onActivityDone() {
        // GIVEN
        val event = ActivityDoneEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )

        val result = listener.handle(event)

        assertFalse(result)
    }

    @Test
    fun onExternalEvent() {
        val event = ExternalEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            name = "order-received"
        )
        val result = listener.handle(event)
        assertFalse(result)
    }

    @Test
    fun onFormSubmitted() {
        // GIVEN
        val event = FormSubmittedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "333",
            activityInstanceId = "777"
        )

        val result = listener.handle(event)
        assertFalse(result)
    }

    @Test
    fun onFormUpdated() {
        val event = FormUpdatedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "111",
        )
        val result = listener.handle(event)
        assertFalse(result)
    }

    @Test
    fun onRunActivityCommand() {
        val event = RunActivityCommand(
            tenantId = 11L,
            activityInstanceId = "111",
            workflowInstanceId = "333"
        )
        val result = listener.handle(event)
        assertFalse(result)
    }
}
