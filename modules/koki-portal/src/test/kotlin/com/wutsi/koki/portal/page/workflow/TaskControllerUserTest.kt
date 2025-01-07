package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.FormFixtures.form
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class TaskControllerUserTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        setUpActivity(activityInstance.copy(activity = activityInstance.activity.copy(type = ActivityType.USER)))

        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SubmitFormDataResponse(formDataId = "111")).whenever(kokiForms).submit(any())
    }

    @Test
    fun show() {
        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)

        verify(kokiForms).html(
            activityInstance.activity.formId!!,
            null,
            null,
            activityInstance.workflowInstance.id,
            activityInstance.id,
            false,
            false
        )

        assertElementNotPresent("#alert-done")
        assertElementNotPresent("#alert-not-assignee")
        assertElementNotPresent("#alert-error")
        assertElementPresent("#form-container")
        assertElementNotPresent("#btn-complete")

        click("#pills-files-tab")
        waitForPresenceOf(".widget-files tr.file")
        assertElementCount(".widget-files tr.file", files.size)

        click("#pills-process-tab", 1000)
        assertElementPresent(".workflow-image img")

        click("#pills-task-tab", 1000)

        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=T1]")
        click("INPUT[value=IMM]")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.TASK_COMPLETED)
        click("#btn-ok")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `activity without assignee`() {
        // GIVEN
        setUpActivity(
            activityInstance.copy(
                activity = activityInstance.activity.copy(type = ActivityType.USER),
                assigneeUserId = null,
            )
        )

        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)

        assertElementPresent("#alert-not-assignee")

        verify(kokiForms).html(
            activityInstance.activity.formId!!,
            null,
            null,
            activityInstance.workflowInstance.id,
            activityInstance.id,
            true,
            false
        )
    }

    @Test
    fun `activity done`() {
        // GIVEN
        setUpActivity(
            activityInstance.copy(
                activity = activityInstance.activity.copy(type = ActivityType.USER),
                status = WorkflowStatus.DONE,
            )
        )

        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)

        assertElementPresent("#alert-done")

        verify(kokiForms).html(
            activityInstance.activity.formId!!,
            null,
            null,
            activityInstance.workflowInstance.id,
            activityInstance.id,
            true,
            false
        )
    }

    private fun setUpActivity(activityInstance: ActivityInstance) {
        doReturn(GetActivityInstanceResponse(activityInstance)).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)
    }

    private fun generateFormHtml(): String {
        val submitUrl =
            "http://localhost:$port/forms/${form.id}?workflow-instance-id=${workflowInstance.id}&activity-instance-id=${activityInstance.id}"
        return """
            <DIV class='form test'>
              <FORM method='post' action='$submitUrl'>
                <DIV class='form-header'>
                  <H1 class='form-title'>Incident Report</H1>
                </DIV>
                <DIV class='form-body'>
                  <DIV class='section'>
                    <DIV class='section-body'>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Customer Name</SPAN><SPAN class='required'>*</SPAN></LABEL>
                        <INPUT name='customer_name' required/>
                      </DIV>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Customer Email</SPAN><SPAN class='required'>*</SPAN></LABEL>
                        <INPUT name='customer_email' type='email' required/>
                      </DIV>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Marial Status</SPAN></LABEL>
                        <DIV class='radio-container' required>
                          <DIV class='item'>
                            <INPUT name='marital_status' type='radio' value='M'/>
                            <LABEL>Married</LABEL>
                          </DIV>
                          <DIV class='item'>
                            <INPUT name='marital_status' type='radio' value='S'/>
                            <LABEL>Single</LABEL>
                          </DIV>
                        </DIV>
                      </DIV>
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>Case Type</SPAN><SPAN class='required'>*</SPAN></LABEL>
                        <DIV class='checkbox-container' required>
                          <DIV class='item'>
                            <INPUT name='case_type' type='checkbox' value='T1'/>
                            <LABEL>T1</LABEL>
                          </DIV>
                          <DIV class='item'>
                            <INPUT name='case_type' type='checkbox' value='T4'/>
                            <LABEL>T4</LABEL>
                          </DIV>
                          <DIV class='item'>
                            <INPUT name='case_type' type='checkbox' value='IMM'/>
                            <LABEL>IMM</LABEL>
                          </DIV>
                        </DIV>
                      </DIV>
                    </DIV>
                  </DIV>
                </DIV>
                <DIV class='form-footer'>
                  <DIV class='form-button-group'>
                    <BUTTON type='submit'>Submit</BUTTON>
                  </DIV>
                </DIV>
              </FORM>
            </DIV>
        """.trimIndent()
    }
}
