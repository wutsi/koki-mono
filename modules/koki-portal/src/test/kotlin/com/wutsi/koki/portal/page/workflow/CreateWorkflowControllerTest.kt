package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import kotlin.test.Test

class CreateWorkflowControllerTest : AbstractPageControllerTest() {
    private val workflow = Workflow(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
    )

    @Test
    fun success() {
        doReturn(ImportWorkflowResponse(workflow.id)).whenever(kokiWorkflow).importWorkflow(any())
        navigateTo("/workflows/create")
        assertCurrentPageIs(PageName.WORKFLOW_CREATE)

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).getWorkflow(workflow.id)
        doReturn(workflowPictureUrl).whenever(kokiWorkflow).getWorkflowImageUrl(workflow.id)
        input("textarea[name=json]", jsonContent())
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_CREATED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementNotPresent(".alert-danger")
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
        doThrow(ex).whenever(kokiWorkflow).importWorkflow(any())

        navigateTo("/workflows/create")
        input("textarea[name=json]", jsonContent())
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `json not valid`() {
        navigateTo("/workflows/create")
        input("textarea[name=json]", "Invalid content :-)")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        doReturn(ImportWorkflowResponse(workflow.id)).whenever(kokiWorkflow).importWorkflow(any())
        navigateTo("/workflows/create")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_CREATE)
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
