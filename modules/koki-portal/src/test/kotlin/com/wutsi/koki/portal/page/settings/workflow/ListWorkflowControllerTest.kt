package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflows
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import kotlin.test.Test

class ListWorkflowControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/workflows")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)

        assertElementCount("tr.workflow", workflows.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchWorkflowResponse()).whenever(kokiWorkflow)
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

        navigateTo("/settings/workflows")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)

        assertElementNotPresent("tr.workflow")
        assertElementPresent(".empty")
    }

    @Test
    fun `listview to create`() {
        navigateTo("/settings/workflows")
        click(".widget-toolbar .btn-create")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_CREATE)
    }

    @Test
    fun `listview to show`() {
        val workflow = Workflow(
            id = 1L,
            name = "WF-001",
            title = "Workflow #1",
            description = "This is an example of workflow",
            active = true,
            requiresApprover = false,
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
                ),
                Activity(
                    id = 99L,
                    type = ActivityType.END,
                    name = "STOP",
                ),
            ),
        )
        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(any())

        navigateTo("/settings/workflows")
        click("tr.workflow .btn-view")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)
    }

    @Test
    fun `listview to edit`() {
        val workflow = Workflow(
            id = 1L,
            name = "WF-001",
            title = "Workflow #1",
            description = "This is an example of workflow",
            active = true,
            requiresApprover = false,
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
                ),
                Activity(
                    id = 99L,
                    type = ActivityType.END,
                    name = "STOP",
                ),
            ),
        )
        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(any())

        navigateTo("/settings/workflows")
        click("tr.workflow .btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/workflows")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
