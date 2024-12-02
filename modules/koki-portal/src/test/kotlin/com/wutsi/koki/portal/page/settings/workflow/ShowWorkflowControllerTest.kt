package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import org.junit.jupiter.api.BeforeEach
import kotlin.collections.map
import kotlin.test.Test

class ShowWorkflowControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
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

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .roles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(anyOrNull())

        doReturn(SearchFormResponse(listOf(form))).whenever(kokiForms)
            .forms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun show() {
        navigateTo("/settings/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)

        assertElementPresent(".btn-start")
        assertElementPresent(".btn-edit")

        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementCount("tr.activity", workflow.activities.size)
    }

    @Test
    fun `edit button hidden when workflow has instances`() {
        doReturn(
            GetWorkflowResponse(workflow.copy(workflowInstanceCount = 11))
        ).whenever(kokiWorkflow).workflow(workflow.id)

        navigateTo("/settings/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)

        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun edit() {
        navigateTo("/settings/workflows/${workflow.id}")

        click(".btn-edit")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)
    }

    @Test
    fun start() {
        navigateTo("/settings/workflows/${workflow.id}")

        click(".btn-start")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_START)
    }

    @Test
    fun activity() {
        navigateTo("/settings/workflows/${workflow.id}")

        click("tr.activity a")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_ACTIVITY)
    }
}
