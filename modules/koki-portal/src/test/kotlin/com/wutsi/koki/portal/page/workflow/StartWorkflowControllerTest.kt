package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
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
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

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

    private val workflowSummary = WorkflowSummary(
        id = workflow.id,
        name = workflow.name,
        title = workflow.title,
    )

    private val workflowInstance = WorkflowInstance(
        id = "xxx",
        workflowId = workflow.id,
        status = WorkflowStatus.NEW,
    )

    private val fmt = SimpleDateFormat("yyyy-MM-dd")
    private val startAt: String = fmt.format(DateUtils.addDays(Date(), 5))
    private val dueAt: String = fmt.format(DateUtils.addDays(Date(), 14))

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser).roles(any())

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).workflow(workflow.id)
        doReturn(SearchWorkflowResponse(listOf(workflowSummary))).whenever(kokiWorkflow)
            .workflows(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .users(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun start() {
        doReturn(GetWorkflowInstanceResponse(workflowInstance.copy(status = WorkflowStatus.NEW)))
            .whenever(kokiWorkflowInstance)
            .workflowInstance(workflowInstance.id)

        doReturn(CreateWorkflowInstanceResponse(workflowInstance.id)).whenever(kokiWorkflowInstance).create(any())

        navigateTo("/workflows/${workflow.id}/start")

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)

        inputAllFieldsAndSubmit()

        val request = argumentCaptor<CreateWorkflowInstanceRequest>()
        verify(kokiWorkflowInstance).create(request.capture())
        assertEquals(startAt, fmt.format(request.firstValue.startAt))
        assertEquals(dueAt, fmt.format(request.firstValue.dueAt))
        assertEquals(3, request.firstValue.participants.size)
        assertEquals(11L, request.firstValue.approverUserId)
        assertEquals(Participant(roleId = 1, userId = 11L), request.firstValue.participants[0])
        assertEquals(Participant(roleId = 2, userId = 12L), request.firstValue.participants[1])
        assertEquals(Participant(roleId = 3, userId = 13L), request.firstValue.participants[2])
        assertEquals(mapOf("PARAM_1" to "1111", "PARAM_2" to "2222"), request.firstValue.parameters)

        verify(kokiWorkflowInstance, never()).start(any())

        assertCurrentPageIs(PageName.WORKFLOW_STARTED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementNotPresent("#started-message")
        assertElementPresent("#scheduled-message")
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun startNow() {
        doReturn(GetWorkflowInstanceResponse(workflowInstance.copy(status = WorkflowStatus.RUNNING)))
            .whenever(kokiWorkflowInstance)
            .workflowInstance(workflowInstance.id)

        doReturn(CreateWorkflowInstanceResponse(workflowInstance.id)).whenever(kokiWorkflowInstance).create(any())
        doReturn(StartWorkflowInstanceResponse("yyy")).whenever(kokiWorkflowInstance).start(any())

        navigateTo("/workflows/${workflow.id}/start")

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)

        inputAllFieldsAndSubmit(fmt.format(Date()))

        verify(kokiWorkflowInstance).create(any())
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
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.WORKFLOW_START)
        assertElementPresent("[name=approverId]:user-invalid")
        assertElementPresent("[name=participant_1]:user-invalid")
        assertElementPresent("[name=participant_2]:user-invalid")
        assertElementPresent("[name=participant_3]:user-invalid")
        assertElementPresent("[name=parameter_PARAM_1]:user-invalid")
        assertElementPresent("[name=parameter_PARAM_2]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/workflows/${workflow.id}/start")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun inputAllFieldsAndSubmit(start: String = startAt) {
        input("input[name=startAt]", toInputDate(start))
        input("input[name=dueAt]", toInputDate(dueAt))
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
