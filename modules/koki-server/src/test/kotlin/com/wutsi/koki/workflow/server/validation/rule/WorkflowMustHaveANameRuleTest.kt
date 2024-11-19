package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkflowWithApprovalMustHaveApproverRoleRuleTest {
    private val rule = WorkflowWithApprovalMustHaveApproverRoleRule()

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
                    ActivityData(name = "stop", type = ActivityType.STOP),
                )
            )
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `no role`() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                approverRole = null,
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.MANUAL, requiresApproval = true),
                    ActivityData(name = "stop", type = ActivityType.STOP),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("workflow: test", result[0].location)
    }

    @Test
    fun `empty role`() {
        val result = rule.validate(
            WorkflowData(
                name = "test",
                description = "This is a new workflow",
                approverRole = "",
                activities = listOf(
                    ActivityData(name = "start", type = ActivityType.START),
                    ActivityData(name = "invoice", type = ActivityType.MANUAL, requiresApproval = true),
                    ActivityData(name = "stop", type = ActivityType.STOP),
                )
            )
        )

        assertEquals(1, result.size)
        assertEquals("workflow: test", result[0].location)
    }
}
