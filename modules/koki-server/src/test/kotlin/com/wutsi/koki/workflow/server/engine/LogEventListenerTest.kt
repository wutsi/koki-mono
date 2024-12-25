package com.wutsi.koki.workflow.server.engine

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.dto.event.ActivityDoneEvent
import com.wutsi.koki.workflow.dto.event.ActivityStartedEvent
import com.wutsi.koki.workflow.dto.event.ExternalEvent
import com.wutsi.koki.workflow.dto.event.WorkflowDoneEvent
import com.wutsi.koki.workflow.dto.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.server.engine.LogEventListener.Companion.MESSAGE_ACTIVITY_DONE
import com.wutsi.koki.workflow.server.engine.LogEventListener.Companion.MESSAGE_ACTIVITY_STARTED
import com.wutsi.koki.workflow.server.engine.LogEventListener.Companion.MESSAGE_WORKFLOW_DONE
import com.wutsi.koki.workflow.server.engine.LogEventListener.Companion.MESSAGE_WORKFLOW_STARTED
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.LogService
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse

class LogEventListenerTest {
    private val logService = mock<LogService>()
    private val logger: KVLogger = DefaultKVLogger()
    private val listener = LogEventListener(logService, logger)

    @AfterTest
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onWorkflowStarted() {
        val event = WorkflowStartedEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
        )
        val result = listener.handle(event)

        assertTrue(result)
        verify(logService).info(
            MESSAGE_WORKFLOW_STARTED,
            event.workflowInstanceId,
            event.tenantId,
            event.timestamp,
            null,
            emptyMap()
        )
    }

    @Test
    fun onWorkflowDone() {
        val event = WorkflowDoneEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
        )
        val result = listener.handle(event)

        assertTrue(result)
        verify(logService).info(
            MESSAGE_WORKFLOW_DONE,
            event.workflowInstanceId,
            event.tenantId,
            event.timestamp,
            null,
            emptyMap()
        )
    }

    @Test
    fun onActivityStarted() {
        val event = ActivityStartedEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertTrue(result)
        verify(logService).info(
            MESSAGE_ACTIVITY_STARTED,
            event.workflowInstanceId,
            event.tenantId,
            event.timestamp,
            event.activityInstanceId,
            emptyMap()
        )
    }

    @Test
    fun onActivityDone() {
        val event = ActivityDoneEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            activityInstanceId = "777"
        )
        val result = listener.handle(event)

        assertTrue(result)
        verify(logService).info(
            MESSAGE_ACTIVITY_DONE,
            event.workflowInstanceId,
            event.tenantId,
            event.timestamp,
            event.activityInstanceId,
            emptyMap()
        )
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
        verify(logService, never()).info(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun onFormSubmitted() {
        val event = FormSubmittedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "111"
        )
        val result = listener.handle(event)

        assertFalse(result)
        verify(logService, never()).info(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun onFormUpdated() {
        val event = FormUpdatedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "111"
        )
        val result = listener.handle(event)

        assertFalse(result)
        verify(logService, never()).info(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
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
        verify(logService, never()).info(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }
}
