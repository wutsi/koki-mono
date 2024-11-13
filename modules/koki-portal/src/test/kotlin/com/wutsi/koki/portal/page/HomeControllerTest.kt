package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class FormControllerTest : AbstractPageControllerTest() {
    private val formId = "1111"
    private val activityInstanceId = "2222"

    private val forms = listOf(
        FormSummary(
            name = "FRM-001",
            title = "Incident Report",
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchFormResponse(forms)).whenever(kokiForms)
            .search(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        val html = """
            <DIV class='form test'>
              <FORM method='post' action='http://localhost:$port/forms/$formId?aiid=$activityInstanceId'>
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
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `submit form`() {
        // WHEN
        navigateTo("/forms/$formId?aiid=$activityInstanceId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=IMM]")
        click("BUTTON")

        verify(kokiWorkflowEngine).complete(
            activityInstanceId,
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com",
                "marital_status" to "S",
                "case_type" to "IMM"
            )
        )

        assertCurrentPageIs(PageName.FORM_SAVED)
        assertElementPresent(".success-message")
    }

    @Test
    fun `re-submit form`() {
        // GIVEN
        val ex = createHttpClientErrorException(400, ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR)
        doThrow(ex).whenever(kokiWorkflowEngine).complete(any(), any())

        // WHEN
        navigateTo("/forms/$formId?aiid=$activityInstanceId")

        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=IMM]")
        click("BUTTON")

        assertCurrentPageIs(PageName.FORM_SAVED)

        verify(kokiWorkflowEngine).complete(
            activityInstanceId,
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com",
                "marital_status" to "S",
                "case_type" to "IMM"
            )
        )
    }

    @Test
    fun `client side validation`() {
        // WHEN
        navigateTo("/forms/$formId?aiid=$activityInstanceId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        click("BUTTON")

        verify(kokiWorkflowEngine, never()).complete(any(), any())

        assertElementPresent("[name=customer_name]:user-invalid")
        assertElementPresent("[name=customer_email]:user-invalid")
        assertElementCount(".user-invalid", 2)
    }
}
