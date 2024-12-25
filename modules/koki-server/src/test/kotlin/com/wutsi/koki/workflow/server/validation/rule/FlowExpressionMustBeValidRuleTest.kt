package com.wutsi.koki.workflow.server.validation.rule

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.service.WorkflowExpressionEvaluator
import org.mockito.Mockito.mock
import org.springframework.expression.ParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowExpressionMustBeValidRuleTest {
    private val evaluator = mock(WorkflowExpressionEvaluator::class.java)
    private val rule = FlowExpressionMustBeValidRule(evaluator)

    @Test
    fun success() {
        doReturn(true).whenever(evaluator).evaluate(any<String>(), any())

        val result = rule.validate(
            WorkflowData(
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = "invoice"),
                    ActivityData(name = "stop"),
                ),
                flows = listOf(
                    FlowData(from = "start", to = "invoice"),
                    FlowData(from = "invoice", to = "stop", expression = "foo > 10"),
                )
            )
        )

        assertTrue(result.isEmpty())
        verify(evaluator).evaluate(any<String>(), any())
    }

    @Test
    fun error() {
        doThrow(ParseException("expre", 1, "Yo man")).whenever(evaluator).evaluate(any<String>(), any())

        val result = rule.validate(
            WorkflowData(
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = "invoice"),
                    ActivityData(name = "stop"),
                    ActivityData(name = "self"),
                ),
                flows = listOf(
                    FlowData(from = "start", to = "invoice"),
                    FlowData(from = "invoice", to = "stop", expression = "fff"),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("flow: invoice -> stop", result[0].location)
    }
}
