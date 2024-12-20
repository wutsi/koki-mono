package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.WorkflowFixtures.workflowPictureUrl
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowStatus
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StartWorkflowControllerTest : AbstractPageControllerTest() {
    private val workflowInstance = WorkflowInstance(
        id = "xxx",
        workflowId = workflow.id,
        status = WorkflowStatus.NEW,
    )

    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    @Test
    fun startNow() {
        doReturn(GetWorkflowInstanceResponse(workflowInstance.copy(status = WorkflowStatus.RUNNING)))
            .whenever(kokiWorkflowInstances)
            .workflow(workflowInstance.id)

        doReturn(CreateWorkflowInstanceResponse(workflowInstance.id)).whenever(kokiWorkflowInstances).create(any())
        doReturn(StartWorkflowInstanceResponse("yyy")).whenever(kokiWorkflowInstances).start(any())

        navigateTo("/settings/workflows/${workflow.id}/start")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_START)

        inputAllFieldsAndSubmit()

        val request = argumentCaptor<CreateWorkflowInstanceRequest>()
        verify(kokiWorkflowInstances).create(request.capture())
        assertEquals(fmt.format(Date()), fmt.format(request.firstValue.startAt))
        assertNull(request.firstValue.dueAt)
        assertEquals(3, request.firstValue.participants.size)
        assertEquals(11L, request.firstValue.approverUserId)
        assertEquals(Participant(roleId = 1, userId = 11L), request.firstValue.participants[0])
        assertEquals(Participant(roleId = 2, userId = 12L), request.firstValue.participants[1])
        assertEquals(Participant(roleId = 3, userId = 13L), request.firstValue.participants[2])
        assertEquals(mapOf("PARAM_1" to "1111", "PARAM_2" to "2222"), request.firstValue.parameters)

        verify(kokiWorkflowInstances).start(workflowInstance.id)

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_STARTED)
        assertElementAttribute(".workflow-image img", "src", workflowPictureUrl)
        assertElementPresent("#started-message")
        assertElementNotPresent("#scheduled-message")
        assertElementNotPresent(".alert-danger")

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/workflows/${workflow.id}/start")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)
    }

    @Test
    fun failure() {
        val ex = createHttpClientErrorException(
            statusCode = 200,
            errorCode = ErrorCode.FORM_NOT_FOUND,
        )
        doThrow(ex).whenever(kokiWorkflowInstances).create(any())

        navigateTo("/settings/workflows/${workflow.id}/start")
        inputAllFieldsAndSubmit()

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_START)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `missing fields`() {
        navigateTo("/settings/workflows/${workflow.id}/start")
        click("#start-now-0")
        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_START)
        assertElementPresent("[name=title]:user-invalid")
        assertElementPresent("[name=startAt]:user-invalid")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/workflows/${workflow.id}/start")
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
