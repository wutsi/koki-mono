package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FormFixtures.form
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class EditFormControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/forms/${form.id}/edit")
        assertCurrentPageIs(PageName.SETTINGS_FORM_EDIT)

        input(
            "textarea[name=json]",
            """
                {
                    "name": "M-XXX",
                    "title": "This is the new subject",
                    "description": "This is the description",
                    "elements": [
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
                }
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
    fun `json error`() {
        navigateTo("/settings/forms/${form.id}/edit")

        input(
            "textarea[name=json]",
            """
                Hello world
            """.trimIndent()
        )
        scrollToBottom()
        select("select[name=active]", 1)
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
