package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class MyCasesWidgetControllerTest : AbstractPageControllerTest() {
    private val users = listOf(
        UserSummary(id = USER_ID, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
        UserSummary(id = 13L, displayName = "Omam Mbiyick"),
    )

    private val workflows = listOf(
        WorkflowSummary(
            id = 1L,
            name = "WF-001",
            title = "Workflow #1",
            active = true,
            requiresApprover = true,
        ),
        WorkflowSummary(
            id = 2L,
            name = "WF-002",
            title = "Workflow #2",
            active = true,
        ),
    )

    private val workflowInstances = listOf(
        WorkflowInstanceSummary(
            id = "aaaa",
            workflowId = workflows[0].id,
            status = WorkflowStatus.RUNNING,
            approverUserId = 11L,
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        ),
        WorkflowInstanceSummary(
            id = "bbb",
            workflowId = workflows[1].id,
            status = WorkflowStatus.RUNNING,
            approverUserId = 11L,
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .users(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SearchWorkflowResponse(workflows)).whenever(kokiWorkflow)
            .workflows(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        doReturn(SearchWorkflowInstanceResponse(workflowInstances)).whenever(kokiWorkflowInstance)
            .workflows(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
    }

    @Test
    fun show() {
        navigateTo("/workflows/widgets/my-cases")

        assertElementPresent(".my-cases-widget")
        assertElementCount(".my-cases-widget table tr", workflowInstances.size + 1)
    }

    @Test
    fun empty() {
        doReturn(SearchWorkflowInstanceResponse()).whenever(kokiWorkflowInstance)
            .workflows(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/workflows/widgets/my-cases")
        assertElementNotPresent(".my-cases-widget table")
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/my-cases")
        assertElementNotPresent(".my-cases-widget table")
    }
}
