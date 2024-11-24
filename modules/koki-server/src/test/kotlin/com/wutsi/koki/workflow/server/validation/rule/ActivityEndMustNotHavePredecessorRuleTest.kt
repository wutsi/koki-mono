package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityEndMustNotHavePredecessorRuleTest {
    private val rule = ActivityEndMustNotHaveSuccessorRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = "invoice"),
                    ActivityData(name = "stop", type = ActivityType.END),
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
                    ActivityData(name = "invoice"),
                    ActivityData(name = "stop", type = ActivityType.END),
                ),
                flows = listOf(
                    FlowData(from = "start", to = "invoice"),
                    FlowData(from = "invoice", to = "stop", expression = "value>0"),
                    FlowData(from = "stop", to = "invoice"),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("activity: stop", result[0].location)
    }
}