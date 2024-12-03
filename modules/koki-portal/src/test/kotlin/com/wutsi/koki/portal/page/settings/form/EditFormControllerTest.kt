package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class EditFormControllerTest : AbstractPageControllerTest() {
    val form = Form(
        id = "1",
        name = "M-001",
        title = "Incident Form",
        active = true,
        content = FormContent(
            name = "M-001",
            title = "Incident Form",
            elements = listOf(
                FormElement(
                    name = "section1",
                    title = "Section #1",
                    type = FormElementType.SECTION,
                    elements = listOf(
                        FormElement(
                            name = "customer_name",
                            title = "Customer Name",
                            type = FormElementType.TEXT,
                            required = true,
                        ),
                        FormElement(
                            name = "customer_email",
                            title = "Customer Email",
                            type = FormElementType.EMAIL,
                            required = true,
                        ),
                    )
                ),
            ),
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())
    }

    @Test
    fun edit() {
        navigateTo("/settings/forms/${form.id}/edit")
        assertCurrentPageIs(PageName.SETTINGS_FORM_EDIT)

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input(
            "textarea[name=elements]",
            """
                [
                    {
                        "name": "amount",
                        "title": "Payment Amount",
                        "type": "NUMBER",
                        "required":true
                    },
                    {
                        "name": "currency",
                        "title": "Currency",
                        "type": "TEXT",
                        "required":false
                    }
                ]
            """.trimIndent()
        )
        scrollToBottom()
        select("select[name=active]", 1)
        click("button[type=submit]")

        val request = argumentCaptor<SaveFormRequest>()
        verify(kokiForms).update(eq(form.id), request.capture())

        assertEquals("M-XXX", request.firstValue.content.name)
        assertEquals("This is the new subject", request.firstValue.content.title)
        assertEquals(2, request.firstValue.content.elements.size)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_FORM_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/forms/${form.id}/edit")

        scrollToBottom()
        click(".btn-cancel")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiForms).update(any(), any())

        navigateTo("/settings/forms/${form.id}/edit")

        scrollToBottom()
        click("button[type=submit]")
        assertCurrentPageIs(PageName.SETTINGS_FORM_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms/${form.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
