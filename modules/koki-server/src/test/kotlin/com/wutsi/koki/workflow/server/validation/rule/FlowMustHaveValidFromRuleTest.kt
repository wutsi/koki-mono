package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowMustHaveValidFromRuleTest {
    private val rule = FlowMustHaveValidFromRule()

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
                    FlowData(from = "InVoice", to = "bad-precessor-1"),
                    FlowData(from = "xxxx", to = "bad-precessor-2"),
                )
            )
        )

        assertEquals(2, result.size)
        assertEquals("flow: InVoice -> bad-precessor-1", result[0].location)
        assertEquals("flow: xxxx -> bad-precessor-2", result[1].location)
    }
}
