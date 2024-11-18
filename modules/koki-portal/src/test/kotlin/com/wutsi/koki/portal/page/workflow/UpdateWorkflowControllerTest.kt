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
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateWorkflowControllerTest : AbstractPageControllerTest() {
    private val workflow = Workflow(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).workflow(workflow.id)
        doReturn(ImportWorkflowResponse(workflow.id)).whenever(kokiWorkflow).import(any(), any())
    }

    @Test
    fun success() {
        navigateTo("/workflows/${workflow.id}/update")

        assertCurrentPageIs(PageName.WORKFLOW_UPDATE)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)

        input("textarea[name=json]", jsonContent())
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_SUCCESS)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun cancel() {
        navigateTo("/workflows/${workflow.id}/update")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.WORKFLOW_LIST)
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
        doThrow(ex).whenever(kokiWorkflow).import(any(), any())

        navigateTo("/workflows/${workflow.id}/update")
        input("textarea[name=json]", jsonContent())
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_UPDATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `json not valid`() {
        navigateTo("/workflows/${workflow.id}/update")
        input("textarea[name=json]", "Invalid content :-)")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_UPDATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        navigateTo("/workflows/${workflow.id}/update")
        input("textarea[name=json]", "")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_UPDATE)
        assertElementPresent("[name=json]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows/${workflow.id}/update")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun jsonContent(): String {
        return getResourceAsString("/workflow-001.json")
    }
}
