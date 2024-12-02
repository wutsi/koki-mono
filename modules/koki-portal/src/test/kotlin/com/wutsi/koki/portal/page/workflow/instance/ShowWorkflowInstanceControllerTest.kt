package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class ShowWorkflowInstanceControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    private val users = listOf(
        UserSummary(id = 11L, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
        UserSummary(id = 13L, displayName = "Omam Mbiyick"),
    )

    private val form = FormSummary(
        id = "ef00493-403911",
        name = "FMR-001",
        title = "Incident Form",
    )

    private val workflow = Workflow(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
        roleIds = roles.map { role -> role.id },
        parameters = listOf("ORDER_ID"),
        active = true,
        requiresApprover = true,
        approverRoleId = 2L,
        activities = listOf(
            Activity(
                id = 11L,
                type = ActivityType.START,
                name = "START",
                title = "Start"
            ),
            Activity(
                id = 12L,
                type = ActivityType.USER,
                name = "INPUT",
                title = "Input Data",
                description = "User input information about the case",
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
                formId = form.id,
            ),
            Activity(
                id = 13L,
                type = ActivityType.SERVICE,
                name = "INVOICE",
                title = "Generate the invoice",
                description = "Generate invoice using Service X",
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
                formId = form.id,
            ),
            Activity(
                id = 13L,
                type = ActivityType.MANUAL,
                name = "PERFORM_TASK",
                title = "Perform the task",
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
            ),
            Activity(
                id = 99L,
                type = ActivityType.END,
                name = "STOP",
            ),
        ),
    )
    private val workflowInstance = WorkflowInstance(
        id = "xxx",
        workflowId = workflow.id,
        title = "2024",
        status = WorkflowStatus.RUNNING,
        approverUserId = 11L,
        createdAt = Date(),
        startAt = DateUtils.addDays(Date(), 3),
        startedAt = Date(),
        dueAt = DateUtils.addDays(Date(), 7),
        participants = listOf(
            Participant(roleId = 1, userId = 11),
            Participant(roleId = 2, userId = 12),
            Participant(roleId = 3, userId = 13),
        ),
        activityInstances = listOf(
            ActivityInstanceSummary(
                id = "111",
                activityId = workflow.activities[0].id,
                status = WorkflowStatus.DONE,
            ),
            ActivityInstanceSummary(
                id = "222",
                activityId = workflow.activities[1].id,
                status = WorkflowStatus.RUNNING,
                assigneeUserId = users[1].id,
                approval = ApprovalStatus.PENDING,
                approverUserId = 11L,
            )
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .searchRoles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).getWorkflow(workflow.id)

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .searchUsers(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SearchFormResponse(listOf(form))).whenever(kokiForms)
            .searchForms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowInstanceResponse(workflowInstance))
            .whenever(kokiWorkflowInstance)
            .get(workflowInstance.id)
    }

    @Test
    fun show() {
        navigateTo("/workflows/instances/${workflowInstance.id}")

        assertCurrentPageIs(PageName.WORKFLOW_INSTANCE)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementCount("tr.activity", workflow.activities.size)

        Thread.sleep(1000)
        assertElementPresent(".files-widget")
        assertElementPresent(".forms-widget")
    }
}
