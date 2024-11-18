package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class WorkflowControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
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
                formId = "109320392",
            ),
            Activity(
                id = 11L,
                type = ActivityType.STOP,
                name = "STOP",
            ),
        ),
    )

    private val pictureUrl = "https://picsum.photos/300/100"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .roles(any())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .workflow(workflow.id)

        doReturn(pictureUrl).whenever(kokiWorkflow)
            .imageUrl(workflow.id)
    }

    @Test
    fun show() {
        navigateTo("/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.WORKFLOW)

        assertElementAttribute(".workflow-image img", "src", pictureUrl)
        assertElementCount("tr.activity", workflow.activities.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}