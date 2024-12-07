package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.WorkflowFixtures.workflowPictureUrl
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import kotlin.test.Test

class CreateWorkflowControllerTest : AbstractPageControllerTest() {
    @Test
    fun success() {
        doReturn(ImportWorkflowResponse(workflow.id)).whenever(kokiWorkflow).import(any())
        navigateTo("/settings/workflows/create")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_CREATE)

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).workflow(workflow.id)
        doReturn(workflowPictureUrl).whenever(kokiWorkflow).imageUrl(workflow.id)
        input("textarea[name=json]", jsonContent())
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_SAVED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementNotPresent(".alert-danger")

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/workflows/create")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)
    }

    @Test
    fun failure() {
        val ex = createHttpClientErrorException(
            statusCode = 200,
            errorCode = ErrorCode.FORM_NOT_FOUND,
            param = "FRM-001",
            data = mapOf(
                "0000" to "Error 1",
                "0001" to "Error 2",
                "0001" to "Error 3",
            )
        )
        doThrow(ex).whenever(kokiWorkflow).import(any())

        navigateTo("/settings/workflows/create")
        input("textarea[name=json]", jsonContent())
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `json not valid`() {
        navigateTo("/settings/workflows/create")
        input("textarea[name=json]", "Invalid content :-)")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        doReturn(ImportWorkflowResponse(workflow.id)).whenever(kokiWorkflow).import(any())
        navigateTo("/settings/workflows/create")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_CREATE)
        assertElementPresent("[name=json]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun jsonContent(): String {
        return getResourceAsString("/workflow-001.json")
    }
}
