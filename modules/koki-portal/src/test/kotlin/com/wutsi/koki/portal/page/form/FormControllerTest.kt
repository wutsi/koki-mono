package com.wutsi.koki.portal.page.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class FormControllerTest : AbstractPageControllerTest() {
    private val formId = "1111"

    private val form = Form(
        id = "309302",
        name = "FRM-001",
        title = "Incident Report",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetFormResponse(form)).whenever(kokiForms).get(any())

        val html = generateFormHtml("http://localhost:$port/forms/$formId")
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull())
    }

    @Test
    fun submit() {
        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=IMM]")
        click("BUTTON")

        verify(kokiFormData).submit(
            formId,
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com",
                "marital_status" to "S",
                "case_type" to "IMM"
            )
        )

        assertCurrentPageIs(PageName.FORM_SAVED)
    }

    @Test
    fun `client side validation`() {
        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        click("BUTTON")

        verify(kokiWorkflowEngine, never()).complete(any(), any())

        assertElementPresent("[name=customer_name]:user-invalid")
        assertElementPresent("[name=customer_email]:user-invalid")
        assertElementCount(".user-invalid", 2)
    }

    @Test
    fun `form not found`() {
        // GIVEN
        val ex = createHttpClientErrorException(404, ErrorCode.FORM_NOT_FOUND)
        doThrow(ex).whenever(kokiForms).html(any(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun update() {
        // GIVEN
        val formDataId = "4094509"

        val html = generateFormHtml("http://localhost:$port/forms/$formId/$formDataId")
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId/$formDataId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=T1]")
        click("INPUT[value=IMM]")
        click("BUTTON")

        val dataArg = argumentCaptor<Map<String, Any>>()
        verify(kokiFormData).update(eq(formDataId), dataArg.capture())
        val data = dataArg.firstValue
        assertEquals(4, data.size)
        assertEquals("Ray Sponsible", data["customer_name"])
        assertEquals("ray.sponsible@gmail.com", data["customer_email"])
        assertEquals("S", data["marital_status"])
        assertEquals(2, (data["case_type"] as Array<*>).size)
        assertEquals("T1", (data["case_type"] as Array<*>)[0])
        assertEquals("IMM", (data["case_type"] as Array<*>)[1])

        assertCurrentPageIs(PageName.FORM_SAVED)
    }

    @Test
    fun `login required`() {
        // GIVEN
        setUpAnonymousUser()

        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun generateFormHtml(submitUrl: String): String {
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