package com.wutsi.koki.workflow.server.validation.rule

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.dto.ActivityData

class ActivityMustNotHaveSelfAsPredecessorRuleTest {
    private val rule = ActivityMustNotHaveSelfAsPredecessorRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "new",
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
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = "invoice", predecessors = listOf("start")),
                    ActivityData(name = "stop", predecessors = listOf("invoice")),
                    ActivityData(name = "self", predecessors = listOf("self")),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("activity: self", result[0].location)
    }
}
