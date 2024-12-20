package com.wutsi.koki.workflow.server.engine.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.message.server.domain.MessageEntity
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.UserService
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityEntity
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import com.wutsi.koki.workflow.server.exception.NoAssigneeException
import com.wutsi.koki.workflow.server.exception.NoMessageException
import com.wutsi.koki.workflow.server.service.ActivityService
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import com.wutsi.koki.workflow.server.service.runner.SendRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

class SendRunnerTest {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val userService = mock<UserService>()
    private val messagingServiceBuilder = mock<MessagingServiceBuilder>()
    private val configurationService = mock<ConfigurationService>()
    private val activityService = mock<ActivityService>()
    private val messageService = mock<MessageService>()
    private val messagingService = mock<MessagingService>()
    private val logService = mock<LogService>()
    private val engine = mock<WorkflowEngine>()
    private val logger = DefaultKVLogger()

    private val executor = SendRunner(
        objectMapper = objectMapper,
        userService = userService,
        workflowInstanceService = workflowInstanceService,
        messagingServiceBuilder = messagingServiceBuilder,
        configurationService = configurationService,
        activityService = activityService,
        templateEngine = templateEngine,
        messageService = messageService,
        logService = logService,
        logger = logger,
    )

    private val tenantId = 1L
    private val message = MessageEntity(
        id = "7777",
        tenantId = tenantId,
        name = "M-1000",
        subject = "Sample email",
        body = "Hello {{recipient}}. This is a message from {{origin}}",
    )
    private val activity = ActivityEntity(
        id = 333L,
        tenantId = tenantId,
        type = ActivityType.SEND,
        messageId = message.id
    )
    private val user = UserEntity(
        id = 777L,
        tenantId = tenantId,
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
    )
    private val activityInstance = ActivityInstanceEntity(
        id = "111",
        tenantId = tenantId,
        workflowInstanceId = "1111",
        activityId = activity.id!!,
        assigneeId = user.id!!,
        status = WorkflowStatus.RUNNING,
    )
    val workflowInstance = WorkflowInstanceEntity(
        id = activityInstance.workflowInstanceId,
        tenantId = tenantId,
        state = "{\"origin\":\"hell\"}"
    )

    @BeforeEach
    fun setUp() {
        doReturn(message).whenever(messageService).get(message.id!!, tenantId)

        doReturn(activity).whenever(activityService).get(activity.id!!)

        doReturn(user).whenever(userService).get(user.id!!, tenantId)

        doReturn(workflowInstance).whenever(workflowInstanceService)
            .get(activityInstance.workflowInstanceId, activityInstance.tenantId)

        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService)
            .search(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(messagingService).whenever(messagingServiceBuilder).build(anyOrNull(), anyOrNull())
    }

    @Test
    fun run() {
        executor.run(activityInstance, engine)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())
        assertEquals("Sample email", msg.firstValue.subject)
        assertEquals("Hello Ray Sponsible. This is a message from hell", msg.firstValue.body)
        assertEquals(user.displayName, msg.firstValue.recipient.displayName)
        assertEquals(user.email, msg.firstValue.recipient.email)

        verify(engine).done(activityInstance.id!!, emptyMap(), tenantId)
    }

    @Test
    fun noMessage() {
        doReturn(activity.copy(messageId = null)).whenever(activityService).get(activity.id!!)

        assertThrows<NoMessageException> {
            executor.run(activityInstance, engine)
        }

        verify(messagingService, never()).send(any())
        verify(engine, never()).done(any(), any(), any())
    }

    @Test
    fun noRecipient() {
        assertThrows<NoAssigneeException> {
            executor.run(activityInstance.copy(assigneeId = null), engine)
        }

        verify(messagingService, never()).send(any())
        verify(engine, never()).done(any(), any(), any())
    }

    @Test
    fun `not running`() {
        executor.run(activityInstance.copy(status = WorkflowStatus.DONE), engine)

        verify(messagingService, never()).send(any())
        verify(engine, never()).done(any(), any(), any())
    }
}
