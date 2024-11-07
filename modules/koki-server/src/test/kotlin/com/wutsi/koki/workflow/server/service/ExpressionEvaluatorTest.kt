package com.wutsi.koki.workflow.server.service

import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.ParameterEntity
import com.wutsi.koki.workflow.server.domain.StateEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.springframework.expression.ParseException
import kotlin.test.Test
import kotlin.test.assertTrue

class ExpressionEvaluatorTest {
    private val evaluator = ExpressionEvaluator()

    private val workflowInstance = WorkflowInstanceEntity(
        parameters = listOf(
            ParameterEntity(name = "WORK_TYPE", value = "T1")
        ),
        state = listOf(
            StateEntity(name = "client_name", value = "Ray Sponsible"),
            StateEntity(name = "client_email", value = "ray.sponsible@gmail.com"),
            StateEntity(name = "submit", value = "true"),
            StateEntity(name = "amount", value = "10000.0"),
            StateEntity(name = "client_id", value = "5"),
            StateEntity(name = "new_client", value = "false")
        )
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
