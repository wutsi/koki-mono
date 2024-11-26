package com.wutsi.koki.portal.page.form

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
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
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

class ShowActivityInstanceControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    private val users = listOf(
        UserSummary(id = 11L, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
    )

    private val workflow = WorkflowSummary(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        active = true,
        requiresApprover = true,
    )

    private val form = FormSummary(
        id = "FMR-001",
        name = "FMR-001",
        title = "Incident Form",
    )

    private val activityInstance = ActivityInstance(
        id = "222",
        activity = Activity(
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
        workflowInstance = WorkflowInstanceSummary(
            id = "4304390-43094039",
            workflowId = workflow.id
        ),
        status = WorkflowStatus.RUNNING,
        assigneeUserId = users[1].id,
        approval = ApprovalStatus.UNKNOWN,
        approverUserId = users[0].id,
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

        doReturn(SearchFormResponse(listOf(form))).whenever(kokiForms)
            .searchForms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetActivityInstanceResponse(activityInstance))
            .whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)
    }

    @Test
    fun show() {
        navigateTo("/workflows/instances/activities/${activityInstance.id}")

        assertCurrentPageIs(PageName.ACTIVITY_INSTANCE)
    }
}
