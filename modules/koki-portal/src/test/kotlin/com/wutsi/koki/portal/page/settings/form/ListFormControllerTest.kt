package com.wutsi.koki.portal.page.settings.form

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
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ListFormControllerTest : AbstractPageControllerTest() {
    private val forms = listOf(
        FormSummary(
            id = "1",
            name = "FRM-001",
            title = "Indicent Manager",
            active = true,
        ),
        FormSummary(
            id = "2",
            name = "FRM-002",
            title = "Reimbursement Form",
            active = true,
        ),
        FormSummary(
            id = "3",
            name = "FRM-003",
            title = "Receipt Submission",
            active = false,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchFormResponse(forms)).whenever(kokiForms)
            .searchForms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    @Test
    fun list() {
        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.FORM_LIST)

        assertElementCount("tr.form", forms.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchFormResponse()).whenever(kokiForms)
            .searchForms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.FORM_LIST)

        assertElementNotPresent("tr.form")
        assertElementPresent(".empty")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun view() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).getForm(any())

        navigateTo("/settings/forms")
        click("tr.form .btn-view")
        assertCurrentPageIs(PageName.MESSAGE)
    }

    @Test
    fun edit() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).getForm(any())

        navigateTo("/settings/forms")
        click("tr.form .btn-edit")
        assertCurrentPageIs(PageName.MESSAGE_EDIT)
    }

    @Test
    fun create() {
        navigateTo("/settings/forms")
        click(".btn-create")
        assertCurrentPageIs(PageName.MESSAGE_CREATE)
    }

    @Test
    fun preview() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).getForm(any())

        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .getFormHtml(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/settings/forms")
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
