package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkflowMustHaveAtOneStartActivityRuleTest {
    private val rule = WorkflowMustHaveOneStartActivityRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.MANUAL),
                    ActivityData(name = "stop", type = ActivityType.END),
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
                    ActivityData(name = "start", type = ActivityType.SERVICE),
                    ActivityData(name = "invoice", type = ActivityType.MANUAL),
                    ActivityData(name = "stop", type = ActivityType.END),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("workflow: test", result[0].location)
    }
}
