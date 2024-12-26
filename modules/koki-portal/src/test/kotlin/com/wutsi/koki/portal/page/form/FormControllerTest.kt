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
import com.wutsi.koki.FormFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class FormControllerTest : AbstractPageControllerTest() {
    private val formId = FormFixtures.form.id
    private val form = FormFixtures.form

    private val fileId = "32093209"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val html = generateFormHtml("http://localhost:$port/forms/$formId")
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SubmitFormDataResponse("1111")).whenever(kokiForms).submit(any())
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
        click("button[type=submit]")

        val request = argumentCaptor<SubmitFormDataRequest>()
        verify(kokiForms).submit(request.capture())

        assertEquals(formId, request.firstValue.formId)
        assertEquals(null, request.firstValue.activityInstanceId)
        assertEquals(null, request.firstValue.workflowInstanceId)
        assertEquals(
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com",
                "marital_status" to "S",
                "case_type" to "IMM"
            ),
            request.firstValue.data
        )

        assertCurrentPageIs(PageName.FORM_SUBMITTED)
    }

    @Test
    fun `submit with workflow`() {
        val html =
            generateFormHtml("http://localhost:$port/forms/$formId?workflow-instance-id=111&activity-instance-id=222")
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId?workflow-instance-id=111&activity-instance-id=222")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=IMM]")
        click("button[type=submit]")

        val request = argumentCaptor<SubmitFormDataRequest>()
        verify(kokiForms).submit(request.capture())

        assertEquals(formId, request.firstValue.formId)
        assertEquals("222", request.firstValue.activityInstanceId)
        assertEquals("111", request.firstValue.workflowInstanceId)
        assertEquals(
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com",
                "marital_status" to "S",
                "case_type" to "IMM"
            ),
            request.firstValue.data
        )

        assertCurrentPageIs(PageName.FORM_SUBMITTED)
    }

    @Test
    fun `client side validation`() {
        // GIVEN
        val html = generateFormHtmlWithFileUpload(
            "http://localhost:$port/forms/$formId",
            "http://localhost:$port/storage",
            true,
            false
        )
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        click("button[type=submit]")

        verify(kokiWorkflowInstances, never()).complete(any(), any())

        assertElementPresent("[name=customer_name]:user-invalid")
        assertElementPresent("[name=customer_email]:user-invalid")
        assertElementCount(".user-invalid", 3)
    }

    @Test
    fun `clear form`() {
        // GIVEN
        val html = generateFormHtmlWithFileUpload(
            "http://localhost:$port/forms/$formId",
            "http://localhost:$port/storage",
            true,
            true
        )
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        assertElementAttribute("input[name=var1]", "value", fileId)

        click(".btn-close")
        assertElementAttribute("input[name=var1]", "value", "")
        assertElementText("span[data-name=var1-filename]", "")
    }

    @Test
    fun `form not found`() {
        // GIVEN
        val ex = createHttpClientErrorException(404, ErrorCode.FORM_NOT_FOUND)
        doThrow(ex).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

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
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId/$formDataId?activity-instance-id=222")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=T1]")
        click("INPUT[value=IMM]")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateFormDataRequest>()
        verify(kokiForms).submit(eq(formDataId), request.capture())
        assertEquals(null, request.firstValue.activityInstanceId)

        val data = request.firstValue.data
        assertEquals(4, data.size)
        assertEquals("Ray Sponsible", data["customer_name"])
        assertEquals("ray.sponsible@gmail.com", data["customer_email"])
        assertEquals("S", data["marital_status"])
        assertEquals(2, (data["case_type"] as Array<*>).size)
        assertEquals("T1", (data["case_type"] as Array<*>)[0])
        assertEquals("IMM", (data["case_type"] as Array<*>)[1])

        assertCurrentPageIs(PageName.FORM_SUBMITTED)
        click(".btn-ok")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `update with workflow`() {
        // GIVEN
        val formDataId = "4094509"

        val html = generateFormHtml("http://localhost:$port/forms/$formId/$formDataId?activity-instance-id=222")
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        navigateTo("/forms/$formId/$formDataId?activity-instance-id=222")

        // THEN
        assertCurrentPageIs(PageName.FORM)
        input("INPUT[name=customer_name]", "Ray Sponsible")
        input("INPUT[name=customer_email]", "ray.sponsible@gmail.com")
        click("INPUT[value=S]")
        click("INPUT[value=T1]")
        click("INPUT[value=IMM]")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateFormDataRequest>()
        verify(kokiForms).submit(eq(formDataId), request.capture())
        assertEquals("222", request.firstValue.activityInstanceId)

        val data = request.firstValue.data
        assertEquals(4, data.size)
        assertEquals("Ray Sponsible", data["customer_name"])
        assertEquals("ray.sponsible@gmail.com", data["customer_email"])
        assertEquals("S", data["marital_status"])
        assertEquals(2, (data["case_type"] as Array<*>).size)
        assertEquals("T1", (data["case_type"] as Array<*>)[0])
        assertEquals("IMM", (data["case_type"] as Array<*>)[1])

        assertCurrentPageIs(PageName.FORM_SUBMITTED)
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

    private fun generateFormHtmlWithFileUpload(
        submitUrl: String,
        downloadUrl: String,
        required: Boolean = true,
        withFile: Boolean = true
    ): String {
        val req = if (required) " required" else ""
        val file = if (withFile) {
            """
                <A class='filename' href='$downloadUrl/11111/foo.txt'>foo.txt</A>
                <button class='btn-close' type='button' name='var1-close' rel='var1'></button>
            """.trimIndent()
        } else {
            ""
        }
        val value = if (withFile) fileId else ""

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
                      <DIV class='section-item'>
                        <LABEL class='title'><SPAN>test</SPAN></LABEL>
                        <DIV class='description'>This is the description</DIV>
                        <DIV class='file-upload-container'>
                          <INPUT type='hidden' name='var1' value="$value" $req/>
                          <BUTTON type='button' class='btn-upload' rel='var1'>Upload File</BUTTON>
                          <INPUT type='file' name='var1-file' rel='var1' data-upload-url='https://foo.com/storage/upload'/>
                          <SPAN data-name='var1-filename'>
                            $file
                          </SPAN>
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
