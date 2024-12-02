package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
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
}
