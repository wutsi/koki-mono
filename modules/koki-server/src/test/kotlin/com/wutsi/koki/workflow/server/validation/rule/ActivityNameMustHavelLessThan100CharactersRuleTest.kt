package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityNameMustHavelLessThan100CharactersRuleTest {
    private val rule = ActivityNameMustHavelLessThan100CharactersRule()

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
        val nameTooLong = "1".repeat(100) + "1"
        val result = rule.validate(
            WorkflowData(
                name = "new",
                description = "This is a new workflow",
                activities = listOf(
                    ActivityData(name = "start"),
                    ActivityData(name = nameTooLong, predecessors = listOf("start")),
                    ActivityData(name = "stop", predecessors = listOf(nameTooLong)),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("activity: " + nameTooLong, result[0].location)
    }
}
