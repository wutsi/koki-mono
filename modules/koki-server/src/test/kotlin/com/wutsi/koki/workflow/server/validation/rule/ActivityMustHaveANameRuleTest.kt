package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityMustHaveANameRuleTest {
    private val rule = ActivityMustHaveANameRule()

    @Test
    fun success() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                approverRole = "foo",
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.MANUAL, requiresApproval = true),
                    ActivityData(name = "stop", type = ActivityType.END),
                )
            )
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun failure() {
        val result = rule.validate(
            WorkflowData(
                name = "",
                description = "This is a new workflow",
                approverRole = null,
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = " ", type = ActivityType.MANUAL, requiresApproval = true),
                    ActivityData(name = "", type = ActivityType.END),
                )
            )
        )

        assertEquals(2, result.size)
    }
}
