package com.wutsi.koki.portal.page.settings.script

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ScriptFixtures.script
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.dto.UpdateScriptRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class EditScriptControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/scripts/${script.id}/edit")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_EDIT)
        assertElementNotPresent(".alert-danger")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        scrollToBottom()
        input("textarea[name=parameters]", "var1\nvar2")
        select("select[name=language]", 2)
        inputCodeMiror("print(\"Hello\")")
        select("select[name=active]", 1)
        click("button[type=submit]")

        val request = argumentCaptor<UpdateScriptRequest>()
        verify(kokiScripts).update(eq(script.id), request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.title)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals(listOf("var1", "var2"), request.firstValue.parameters)
        assertEquals(Language.PYTHON, request.firstValue.language)
        assertEquals("print(\"Hello\")", request.firstValue.code)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/scripts/${script.id}/edit")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        scrollToBottom()
        input("textarea[name=parameters]", "var1\nvar2")
        select("select[name=language]", 2)
        inputCodeMiror("print(\"Hello\")")
        select("select[name=active]", 1)
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.SCRIPT_IN_USE)
        doThrow(ex).whenever(kokiScripts).update(any(), any())

        navigateTo("/settings/scripts/${script.id}/edit")

        input("input[name=name]", "M-XXX")
        input("input[name=title]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        scrollToBottom()
        input("textarea[name=parameters]", "var1\nvar2")
        select("select[name=language]", 2)
        inputCodeMiror("print(\"Hello\")")
        select("select[name=active]", 1)
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/scripts/${script.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
