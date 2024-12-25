package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.expression.ExpressionEvaluator
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class WorkflowExpressionEvaluatorTest {
    private val objectMapper = ObjectMapper()
    private val delegate = mock<ExpressionEvaluator>()
    private val evaluator = WorkflowExpressionEvaluator(objectMapper, delegate)

    private val state = mapOf(
        "WORK_TYPE" to "T1",
        "client_name" to "Ray Sponsible",
        "client_email" to "ray.sponsible@gmail.com",
        "submit" to "true",
        "amount" to "10000.0",
        "taxes" to 15.0,
        "client_id" to "5",
        "age" to 30,
        "new_client" to false,
        "status" to listOf("M", "F"),
    )
    private val workflowInstance = WorkflowInstanceEntity(
        state = objectMapper.writeValueAsString(state)
    )

    @Test
    fun evaluate() {
        doReturn(true).whenever(delegate).evaluate(any(), any())

        val flow = FlowEntity(expression = "client_email == 'ray.sponsible@gmail.com'")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `empty expression`() {
        val flow = FlowEntity(expression = "")

        verify(delegate, never()).evaluate(any(), any())
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `null expression`() {
        val flow = FlowEntity(expression = null)

        verify(delegate, never()).evaluate(any(), any())
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }
}
