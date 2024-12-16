package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityReceiveMustHaveAnEventRuleTest {
    private val rule = ActivityReceiveMustHaveAnEventRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.RECEIVE, event = "employee"),
                    ActivityData(name = "stop"),
                ),
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
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.RECEIVE, event = null),
                    ActivityData(name = "quote", type = ActivityType.RECEIVE, event = ""),
                    ActivityData(name = "stop"),
                ),
            )
        )

        assertEquals(2, result.size)
        assertEquals("activity: invoice", result[0].location)
        assertEquals("activity: quote", result[1].location)
    }
}
