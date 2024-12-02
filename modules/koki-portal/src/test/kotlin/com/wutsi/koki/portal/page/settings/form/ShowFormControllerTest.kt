package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ShowFormControllerTest : AbstractPageControllerTest() {
    val form = Form(
        id = "1",
        name = "M-001",
        title = "Message #1",
        active = true,
        content = FormContent(
            elements = listOf(
                FormElement(type = FormElementType.TEXT, name = "name"),
                FormElement(type = FormElementType.PARAGRAPH, name = "description"),
            )
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetFormResponse(form)).whenever(kokiForms).getForm(any())
    }

    @Test
    fun show() {
        navigateTo("/settings/forms/${form.id}")
        assertCurrentPageIs(PageName.FORM)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms/${form.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiForms).deleteForm(form.id)
        assertCurrentPageIs(PageName.FORM_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.FORM_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiForms, never()).deleteForm(any())
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiForms).deleteForm(any())

        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.FORM)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.FORM_EDIT)
    }


    @Test
    fun preview() {
        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .getFormHtml(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/settings/forms/${form.id}")
        click(".btn-preview")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }

    private fun generateFormHtml(): String {
        return """
            <DIV class='form test'>
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
            </DIV>
        """.trimIndent()
    }
}
