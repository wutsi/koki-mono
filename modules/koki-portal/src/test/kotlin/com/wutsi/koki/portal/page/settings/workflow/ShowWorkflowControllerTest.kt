package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import kotlin.test.Test

class ShowWorkflowControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/workflows/${workflow.id}")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)

        assertElementPresent(".btn-start")
        assertElementPresent(".btn-edit")

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
