package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityMustNotBeOrphanRuleTest {
    private val rule = ActivityMustNotBeOrphanRule()

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
                    ActivityData(name = "orphan"),
                )
            )
        )

        assertEquals("activity: orphan", result[0].location)
    }
}
