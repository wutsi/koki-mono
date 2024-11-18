package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ListWorkflowControllerTest : AbstractPageControllerTest() {
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
        WorkflowSummary(
            id = 3L,
            name = "WF-003",
            title = "Workflow #3",
            active = false,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchWorkflowResponse(workflows)).whenever(kokiWorkflow)
            .workflows(any(), anyOrNull(), anyOrNull())
    }

    @Test
    fun list() {
        navigateTo("/workflows")
        assertCurrentPageIs(PageName.WORKFLOW_LIST)

        assertElementCount("tr.workflow", workflows.size)
    }

    @Test
    fun `listview to create`() {
        navigateTo("/workflows")
        click(".widget-toolbar .btn-create")

        assertCurrentPageIs(PageName.WORKFLOW_CREATE)
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
                    type = ActivityType.STOP,
                    name = "STOP",
                ),
            ),
        )
        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(any())

        navigateTo("/workflows")
        click("tr.workflow .btn-view")

        assertCurrentPageIs(PageName.WORKFLOW)
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
                    type = ActivityType.STOP,
                    name = "STOP",
                ),
            ),
        )
        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(any())

        navigateTo("/workflows")
        click("tr.workflow .btn-edit")

        assertCurrentPageIs(PageName.WORKFLOW_EDIT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
