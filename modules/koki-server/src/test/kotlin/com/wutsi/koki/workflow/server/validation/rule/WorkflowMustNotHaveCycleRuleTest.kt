package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkflowMustNotHaveCycleRuleTest {
    private val rule = WorkflowMustNotHaveCycleRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = "invoice", predecessors = listOf("start")),
                    ActivityData(name = "stop", predecessors = listOf("invoice")),
                )
            )
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun error() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start", predecessors = listOf("stop")),
                    ActivityData(name = "invoice", predecessors = listOf("start")),
                    ActivityData(name = "stop", predecessors = listOf("invoice")),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("workflow: test", result[0].location)
    }
}
