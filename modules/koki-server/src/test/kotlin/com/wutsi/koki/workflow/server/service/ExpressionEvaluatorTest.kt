package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.springframework.expression.ParseException
import kotlin.test.Test
import kotlin.test.assertTrue

class ExpressionEvaluatorTest {
    private val objectMapper = ObjectMapper()
    private val evaluator = ExpressionEvaluator(objectMapper)

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
    fun `evaluate from state`() {
        val flow = FlowEntity(expression = "client_email == 'ray.sponsible@gmail.com'")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `evaluate from parameter`() {
        val flow = FlowEntity(expression = "WORK_TYPE != 'T2'")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `evaluate decimal`() {
        val flow = FlowEntity(expression = "amount > 5000.0")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `evaluate numeric`() {
        val flow = FlowEntity(expression = "client_id > 1")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `evaluate boolean - true`() {
        val flow = FlowEntity(expression = "submit == true")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `evaluate boolean - false`() {
        val flow = FlowEntity(expression = "!new_client")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `empty expression`() {
        val flow = FlowEntity(expression = "")
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `null expression`() {
        val flow = FlowEntity(expression = null)
        assertTrue(evaluator.evaluate(flow, workflowInstance))
    }

    @Test
    fun `malformed expression`() {
        val flow = FlowEntity(expression = "|\\")
        assertThrows<ParseException> { evaluator.evaluate(flow, workflowInstance) }
    }

    @Test
    fun `unknown variable`() {
        val flow = FlowEntity(expression = "unknown=5")
        assertFalse(evaluator.evaluate(flow, workflowInstance))
    }
}
