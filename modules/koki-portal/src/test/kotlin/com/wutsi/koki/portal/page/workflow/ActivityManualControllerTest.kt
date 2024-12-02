package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class ActivityManualControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    private val users = listOf(
        UserSummary(id = USER_ID, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
    )

    private val workflow = WorkflowSummary(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        active = true,
        requiresApprover = true,
    )

    private val activityInstance = ActivityInstance(
        id = "222",
        activity = Activity(
            id = 12L,
            type = ActivityType.MANUAL,
            name = "INPUT",
            title = "Input Data",
            description = "User input information about the case",
            roleId = roles[0].id,
            active = true,
            requiresApproval = true,
        ),
        workflowInstance = WorkflowInstanceSummary(
            id = "4304390-43094039",
            workflowId = workflow.id
        ),
        status = WorkflowStatus.RUNNING,
        assigneeUserId = USER_ID,
        approval = ApprovalStatus.UNKNOWN,
        createdAt = DateUtils.addDays(Date(), -10),
        startedAt = DateUtils.addDays(Date(), -5),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .searchRoles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SearchWorkflowResponse(listOf(workflow))).whenever(kokiWorkflow)
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

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .searchUsers(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetActivityInstanceResponse(activityInstance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)
    }

    @Test
    fun `show activity and complete`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()
        verify(kokiWorkflowInstance).complete(activityInstance.id, CompleteActivityInstanceRequest())

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY_COMPLETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `show activity and do not complete`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.dismiss()

        verify(kokiWorkflowInstance, never()).complete(any(), any())

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `show error`() {
        // GIVEN
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR)
        doThrow(ex).whenever(kokiWorkflowInstance).complete(any(), any())

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `toolbar not available when activity not running`() {
        // GIVEN
        val instance = activityInstance.copy(status = WorkflowStatus.NEW)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".btn-activity-manual-complete")
    }

    @Test
    fun `toolbar not available for another assignee`() {
        // GIVEN
        val instance = activityInstance.copy(assigneeUserId = 55L)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".btn-activity-manual-complete")
    }
}
