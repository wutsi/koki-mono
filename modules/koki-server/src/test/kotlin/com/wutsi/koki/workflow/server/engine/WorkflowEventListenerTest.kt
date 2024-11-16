package com.wutsi.koki.workflow.server.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkflowEventListenerTest {
    private val workflowEngine = mock<WorkflowEngine>()
    private val activityInstanceService = mock<ActivityInstanceService>()
    private val formDataService = mock<FormDataService>()
    private val objectMapper = ObjectMapper()

    private val listener = WorkflowEventListener(
        workflowEngine = workflowEngine,
        activityInstanceService = activityInstanceService,
        formDataService = formDataService,
        objectMapper = objectMapper
    )

    private val formData = FormDataEntity(
        tenantId = 1L,
        id = "222",
        formId = "111",
        data = """
                {
                    "A": "aa",
                    "B": "bb"
                }
            """.trimIndent()
    )

    private val activityInstance = ActivityInstanceEntity(id = "eeee")

    @BeforeEach
    fun setUp() {
        doReturn(listOf(activityInstance)).whenever(activityInstanceService)
            .search(
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )

        doReturn(formData).whenever(formDataService).get(any<String>(), any<Long>())
    }

    @Test
    fun `form submitted and not linked with activity`() {
        val event = FormSubmittedEvent(
            tenantId = 1,
            formId = formData.formId,
            formDataId = formData.id!!,
        )
        listener.onFormSubmitted(event)

        verify(workflowEngine, never()).done(any(), any())
    }

    @Test
    fun `form submitted linked with activity`() {
        val event = FormSubmittedEvent(
            tenantId = formData.tenantId,
            formId = formData.formId,
            formDataId = formData.id!!,
            activityInstanceId = activityInstance.id
        )
        listener.onFormSubmitted(event)

        val state = argumentCaptor<Map<String, Any>>()
        verify(workflowEngine).done(eq(activityInstance), state.capture())
        assertEquals(2, state.firstValue.size)
        assertEquals("aa", state.firstValue["A"])
        assertEquals("bb", state.firstValue["B"])
    }

    @Test
    fun `form updated and not linked with activity`() {
        val event = FormUpdatedEvent(
            tenantId = 1,
            formId = formData.formId,
            formDataId = formData.id!!,
        )
        listener.onFormUpdated(event)

        verify(workflowEngine, never()).done(any(), any())
    }

    @Test
    fun `form updated linked with activity`() {
        val event = FormUpdatedEvent(
            tenantId = formData.tenantId,
            formId = formData.formId,
            formDataId = formData.id!!,
            activityInstanceId = activityInstance.id
        )
        listener.onFormUpdated(event)

        val state = argumentCaptor<Map<String, Any>>()
        verify(workflowEngine).done(eq(activityInstance), state.capture())
        assertEquals(2, state.firstValue.size)
        assertEquals("aa", state.firstValue["A"])
        assertEquals("bb", state.firstValue["B"])
    }
}
