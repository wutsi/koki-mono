package com.wutsi.koki.portal.page.settings.workflow

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

class EditWorkflowControllerTest : AbstractPageControllerTest() {
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
    fun update() {
        navigateTo("/settings/workflows/${workflow.id}/edit")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)

        input("textarea[name=json]", jsonContent())
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_SAVED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun cancel() {
        navigateTo("/settings/workflows/${workflow.id}/edit")
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
        doThrow(ex).whenever(kokiWorkflow).import(any(), any())

        navigateTo("/settings/workflows/${workflow.id}/edit")
        input("textarea[name=json]", jsonContent())
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `json not valid`() {
        navigateTo("/settings/workflows/${workflow.id}/edit")
        input("textarea[name=json]", "Invalid content :-)")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        navigateTo("/settings/workflows/${workflow.id}/edit")
        input("textarea[name=json]", "")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_EDIT)
        assertElementPresent("[name=json]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/workflows/${workflow.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun jsonContent(): String {
        return getResourceAsString("/workflow-001.json")
    }
}
