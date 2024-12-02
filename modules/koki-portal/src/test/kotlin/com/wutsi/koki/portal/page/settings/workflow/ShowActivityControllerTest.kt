package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.dto.SearchMessageResponse
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

class ShowActivityControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    val form = Form(
        id = "1",
        name = "M-001",
        title = "Form #1",
        active = true,
        content = FormContent(),
    )

    val message = Message(
        id = "11",
        name = "M-001",
        subject = "Message #1",
        active = true,
        body = "Yo Man",
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
                messageId = message.id,
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
                messageId = message.id,
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
            .searchRoles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow)
            .getWorkflow(anyOrNull())

        doReturn(GetFormResponse(form)).whenever(kokiForms).getForm(any())
        doReturn(
            SearchFormResponse(
                listOf(FormSummary(id = form.id, name = form.name, title = form.title))
            )
        ).whenever(kokiForms).searchForms(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        doReturn(GetMessageResponse(message)).whenever(kokiMessages).get(any())
        doReturn(
            SearchMessageResponse(
                listOf(MessageSummary(id = message.id, name = message.name))
            )
        ).whenever(kokiMessages).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun show() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_ACTIVITY)
    }

    @Test
    fun form() {
        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .getFormHtml(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.form")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun message() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.message")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE)
    }

    private fun generateFormHtml(): String {
        return getResourceAsString("/form-readonly.html")
    }
}
