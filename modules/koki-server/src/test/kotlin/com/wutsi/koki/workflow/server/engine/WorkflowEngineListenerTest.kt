package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.dto.event.ActivityDoneEvent
import com.wutsi.koki.workflow.dto.event.ActivityStartedEvent
import com.wutsi.koki.workflow.dto.event.ExternalEvent
import com.wutsi.koki.workflow.dto.event.WorkflowDoneEvent
import com.wutsi.koki.workflow.dto.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.LogService
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class WorkflowEngineListenerTest {
    private val workflowEngine = mock<WorkflowEngine>()
    private val workflowInstanceService = mock<WorkflowInstanceService>()
    private val formDataService = mock<FormDataService>()
    private val objectMapper = ObjectMapper()
    private val eventPublisher = mock<EventPublisher>()
    private val logService = mock<LogService>()
    private val logger: KVLogger = DefaultKVLogger()
    private val listener = WorkflowEventListener(
        workflowEngine = workflowEngine,
        workflowInstanceService = workflowInstanceService,
        formDataService = formDataService,
        objectMapper = objectMapper,
        eventPublisher = eventPublisher,
        logService = logService,
        logger = logger
    )

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
    fun onActivityStarted() {
        val event = ActivityStartedEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            activityInstanceId = "777"
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

        val activity1 = ActivityInstanceEntity(
            id = "333111",
            workflowInstanceId = event.workflowInstanceId,
            tenantId = event.tenantId
        )
        val activity2 = activity1.copy(id = "333222")
        doReturn(listOf(activity1, activity2)).whenever(workflowEngine).next(anyOrNull(), anyOrNull())

        // WHEN
        val result = listener.handle(event)

        // THEN
        assertTrue(result)

        verify(workflowEngine).next(event.workflowInstanceId, event.tenantId)

        val cmd = argumentCaptor<RunActivityCommand>()
        verify(eventPublisher, times(2)).publish(cmd.capture())

        assertEquals(activity1.id, cmd.firstValue.activityInstanceId)
        assertEquals(activity1.workflowInstanceId, cmd.firstValue.workflowInstanceId)
        assertEquals(activity1.tenantId, cmd.firstValue.tenantId)

        assertEquals(activity2.id, cmd.secondValue.activityInstanceId)
        assertEquals(activity2.workflowInstanceId, cmd.secondValue.workflowInstanceId)
        assertEquals(activity2.tenantId, cmd.secondValue.tenantId)
    }

    @Test
    fun onExternalEvent() {
        val event = ExternalEvent(
            tenantId = 11L,
            workflowInstanceId = "333",
            name = "order-received"
        )
        val result = listener.handle(event)

        assertTrue(result)
        verify(workflowEngine).received(event)
    }

    @Test
    fun `onFormSubmitted - complete activity`() {
        // GIVEN
        val event = FormSubmittedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "333",
            activityInstanceId = "777"
        )

        val formData = FormDataEntity(data = "{\"x\":\"xx\"}")
        doReturn(formData).whenever(formDataService).get(event.formDataId, event.tenantId)

        // WHEN
        val result = listener.handle(event)

        // THEN
        assertTrue(result)
        verify(workflowEngine).done(event.activityInstanceId!!, mapOf("x" to "xx"), event.tenantId)
    }

    @Test
    fun `onFormSubmitted - start new workflow`() {
        // GIVEN
        val event = FormSubmittedEvent(
            tenantId = 11L,
            formId = "111",
            formDataId = "333",
        )

        val workflow1 = WorkflowInstanceEntity(id = "333", tenantId = event.tenantId)
        val workflow2 = WorkflowInstanceEntity(id = "555", tenantId = event.tenantId)
        doReturn(listOf(workflow1, workflow2)).whenever(workflowInstanceService).create(event)

        // WHEN
        val result = listener.handle(event)

        // THEN
        assertTrue(result)

        verify(workflowEngine, times(2)).start(any(), any())
        verify(workflowEngine).start(workflow1.id!!, workflow1.tenantId)
        verify(workflowEngine).start(workflow2.id!!, workflow1.tenantId)
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

        assertTrue(result)
        verify(workflowEngine).run(event.activityInstanceId, event.tenantId)
    }

    @Test
    fun `onRunActivityCommand - error`() {
        val ex = IllegalStateException("Failed")
        doThrow(ex).whenever(workflowEngine).run(any(), any())

        val event = RunActivityCommand(
            tenantId = 11L,
            activityInstanceId = "111",
            workflowInstanceId = "333"
        )
        assertThrows<IllegalStateException> { listener.handle(event) }

        verify(logService).error(
            ex.message!!,
            event.workflowInstanceId,
            event.tenantId,
            event.timestamp,
            event.activityInstanceId,
            emptyMap(),
            ex
        )
    }
}
