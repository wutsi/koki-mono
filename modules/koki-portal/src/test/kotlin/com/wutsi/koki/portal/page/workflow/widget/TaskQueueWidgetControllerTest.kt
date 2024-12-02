package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.SetActivityInstanceAssigneeRequest
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class TaskQueueWidgetControllerTest : AbstractPageControllerTest() {
    private val workflows = listOf(
        WorkflowSummary(
            id = 1L,
            name = "WF-001",
            title = "Workflow #1",
            active = true,
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
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        ),
        WorkflowInstanceSummary(
            id = "bbb",
            workflowId = workflows[1].id,
            status = WorkflowStatus.RUNNING,
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        )
    )

    private val activities = listOf(
        ActivitySummary(
            id = 11L,
            workflowId = workflows[0].id,
            type = ActivityType.START,
            name = "START",
            title = "Start"
        ),
        ActivitySummary(
            id = 12L,
            workflowId = workflows[1].id,
            type = ActivityType.USER,
            name = "INPUT",
            title = "Input Data",
        ),
    )

    private val activityInstances = listOf(
        ActivityInstanceSummary(
            id = "1111",
            workflowInstanceId = workflowInstances[0].id,
            activityId = activities[0].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.PENDING,
        ),
        ActivityInstanceSummary(
            id = "222",
            workflowInstanceId = workflowInstances[0].id,
            activityId = activities[0].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.UNKNOWN,
        ),
        ActivityInstanceSummary(
            id = "333",
            workflowInstanceId = workflowInstances[1].id,
            activityId = activities[1].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.UNKNOWN,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchActivityInstanceResponse(activityInstances)).whenever(kokiWorkflowInstance)
            .searchActivities(
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
                anyOrNull()
            )

        doReturn(SearchActivityResponse(activities)).whenever(kokiWorkflow)
            .searchActivities(
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

        doReturn(SearchWorkflowResponse(workflows)).whenever(kokiWorkflow)
            .searchWorkflows(
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
            .searchWorkflows(
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
        navigateTo("/workflows/widgets/task-queue")

        assertElementPresent(".task-queue-widget")
        assertElementCount(".task-queue-widget table tr", activityInstances.size + 1)

        click(".btn-assign")

        val request = argumentCaptor<SetActivityInstanceAssigneeRequest>()
        verify(kokiWorkflowInstance).setAssignee(request.capture())
        assertEquals(USER_ID, request.firstValue.userId)
        assertEquals(listOf(activityInstances[0].id), request.firstValue.activityInstanceIds)
    }

    @Test
    fun `no unassigned activity instance`() {
        doReturn(SearchActivityInstanceResponse()).whenever(kokiWorkflowInstance)
            .searchActivities(
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
                anyOrNull()
            )

        navigateTo("/workflows/widgets/my-tasks")
        assertElementNotPresent(".my-tasks-widget table")
    }

    @Test
    fun `no activity associated with my role`() {
        doReturn(SearchActivityResponse()).whenever(kokiWorkflow)
            .searchActivities(
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

        navigateTo("/workflows/widgets/my-tasks")
        assertElementNotPresent(".my-tasks-widget table")
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/task-queue")
        assertElementNotPresent(".task-queue-widget")
    }
}
