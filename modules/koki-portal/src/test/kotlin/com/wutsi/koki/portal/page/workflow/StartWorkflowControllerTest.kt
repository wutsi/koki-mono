package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.junit.jupiter.api.BeforeEach
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StartWorkflowControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    private val users = listOf(
        UserSummary(id = 11L, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
        UserSummary(id = 13L, displayName = "Omam Mbiyick"),
    )

    private val workflow = Workflow(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
        roleIds = roles.map { it.id },
        requiresApprover = true,
        approverRoleId = 3L,
        parameters = listOf("PARAM_1", "PARAM_2"),
    )

    private val workflowInstance = WorkflowInstance(
        id = "xxx",
        workflowId = workflow.id,
        status = WorkflowStatus.NEW,
    )

    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .searchRoles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).getWorkflow(workflow.id)

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .searchUsers(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun startNow() {
        doReturn(GetWorkflowInstanceResponse(workflowInstance.copy(status = WorkflowStatus.RUNNING)))
            .whenever(kokiWorkflowInstance)
            .get(workflowInstance.id)

        doReturn(CreateWorkflowInstanceResponse(workflowInstance.id)).whenever(kokiWorkflowInstance).create(any())
        doReturn(StartWorkflowInstanceResponse("yyy")).whenever(kokiWorkflowInstance).start(any())

        navigateTo("/workflows/${workflow.id}/start")

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)

        inputAllFieldsAndSubmit()

        val request = argumentCaptor<CreateWorkflowInstanceRequest>()
        verify(kokiWorkflowInstance).create(request.capture())
        assertEquals(fmt.format(Date()), fmt.format(request.firstValue.startAt))
        assertNull(request.firstValue.dueAt)
        assertEquals(3, request.firstValue.participants.size)
        assertEquals(11L, request.firstValue.approverUserId)
        assertEquals(Participant(roleId = 1, userId = 11L), request.firstValue.participants[0])
        assertEquals(Participant(roleId = 2, userId = 12L), request.firstValue.participants[1])
        assertEquals(Participant(roleId = 3, userId = 13L), request.firstValue.participants[2])
        assertEquals(mapOf("PARAM_1" to "1111", "PARAM_2" to "2222"), request.firstValue.parameters)

        verify(kokiWorkflowInstance).start(workflowInstance.id)

        assertCurrentPageIs(PageName.WORKFLOW_STARTED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementPresent("#started-message")
        assertElementNotPresent("#scheduled-message")
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun cancel() {
        navigateTo("/workflows/${workflow.id}/start")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.WORKFLOW)
    }

    @Test
    fun failure() {
        val ex = createHttpClientErrorException(
            statusCode = 200,
            errorCode = ErrorCode.FORM_NOT_FOUND,
        )
        doThrow(ex).whenever(kokiWorkflowInstance).create(any())

        navigateTo("/workflows/${workflow.id}/start")
        inputAllFieldsAndSubmit()

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        navigateTo("/workflows/${workflow.id}/start")
        click("#start-now-0")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementPresent("[name=title]:user-invalid")
        assertElementPresent("[name=startAt]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows/${workflow.id}/start")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun inputAllFieldsAndSubmit(start: String? = null, due: String? = null) {
        input("input[name=title]", "Sample")
        if (start == null) {
            click("#start-now-1")
        } else {
            click("#start-now-0")
            input("input[name=startAt]", toInputDate(start))
        }
        if (due != null) {
            input("input[name=dueAt]", toInputDate(due))
        }
        scrollToMiddle()
        select("select[name=approverId]", 1)
        select("select[name=participant_1]", 1)
        select("select[name=participant_2]", 2)
        select("select[name=participant_3]", 3)
        scrollToBottom()
        input("input[name=parameter_PARAM_1]", "1111")
        input("input[name=parameter_PARAM_2]", "2222")
        click("button[type=submit]")
    }

    private fun toInputDate(date: String): String {
        val xdate = date.replace("-", "")
        val inputDate = xdate.substring(0, 4) + "\t" + xdate.substring(4)
        return inputDate
    }
}
