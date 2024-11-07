package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowMustNotBeSelfRuleTest {
    private val rule = ActivityMustNotHaveSelfAsPredecessorRule()

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
                    FlowData(from = "invoice", to = "stop"),
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
                    ActivityData(name = "invoice", predecessors = listOf("start")),
                    ActivityData(name = "stop", predecessors = listOf("invoice")),
                    ActivityData(name = "self", predecessors = listOf("self")),
                ),
                flows = listOf(
                    FlowData(from = "start", to = "invoice"),
                    FlowData(from = "invoice", to = "stop"),
                    FlowData(from = "self", to = "self"),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("activity: self", result[0].location)
    }
}
