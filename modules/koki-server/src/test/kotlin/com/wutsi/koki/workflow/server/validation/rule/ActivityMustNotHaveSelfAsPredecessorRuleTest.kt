package com.wutsi.koki.workflow.server.validation.rule

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.dto.ActivityData

class ActivityMustHaveValidPredecessorRuleTest {
    private val rule = ActivityMustHaveValidPredecessorRule()

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
                    ActivityData(name = "bad-precessor-1", predecessors = listOf("InVoice")),
                    ActivityData(name = "bad-precessor-2", predecessors = listOf("xxxx")),
                )
            )
        )

        assertEquals(2, result.size)
        assertEquals("activity: bad-precessor-1", result[0].location)
        assertEquals("activity: bad-precessor-2", result[1].location)
    }
}
