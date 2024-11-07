package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowMustHaveValidToRuleTest {
    private val rule = FlowMustHaveValidToRule()

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
                    ActivityData(name = "bad-precessor-1"),
                    ActivityData(name = "bad-precessor-2"),
                ),
                flows = listOf(
                    FlowData(to = "InVoice", from = "bad-precessor-1"),
                    FlowData(to = "xxxx", from = "bad-precessor-2"),
                )
            )
        )

        assertEquals(2, result.size)
        assertEquals("flow: bad-precessor-1 -> InVoice", result[0].location)
        assertEquals("flow: bad-precessor-2 -> xxxx", result[1].location)
    }
}
