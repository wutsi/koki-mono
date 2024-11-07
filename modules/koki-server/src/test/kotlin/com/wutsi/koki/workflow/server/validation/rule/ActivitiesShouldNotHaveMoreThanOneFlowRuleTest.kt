package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivitiesShouldNotHaveMoreThanOneFlowRuleTest {
    private val rule = ActivitiesShouldNotHaveMoreThanOneFlowRule()

    @Test
    fun success() {
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
    }

    @Test
    fun error() {
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
                    FlowData(from = "invoice", to = "stop", expression = "value>0"),
                    FlowData(from = "start", to = "invoice", expression = "value<0"),
                    FlowData(from = "invoice", to = "stop"),
                )
            )
        )

        assertEquals(2, result.size)
        assertEquals("flow: start -> invoice", result[0].location)
        assertEquals("flow: invoice -> stop", result[1].location)
    }
}
