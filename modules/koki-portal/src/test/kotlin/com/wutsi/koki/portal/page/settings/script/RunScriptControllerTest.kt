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
import com.wutsi.koki.script.dto.RunScriptRequest
import com.wutsi.koki.script.dto.UpdateScriptRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class RunScriptControllerTest : AbstractPageControllerTest() {
    @Test
    fun run() {
        navigateTo("/settings/scripts/${script.id}/run")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_RUN)

        assertElementNotPresent("#exec")
        input("textarea[name=parameters]", "var1=100\nvar3=1\n\n")
        select("select[name=language]", 1)
        inputCodeMiror("print(\"Hello\")")
        scrollToBottom()
        click("button[type=submit]")

        val request = argumentCaptor<RunScriptRequest>()
        verify(kokiScripts).run(request.capture())

        assertEquals(
            mapOf(
                "var1" to "100",
                "var3" to "1"
            ), request.firstValue.parameters
        )
        assertEquals(Language.PYTHON, request.firstValue.language)
        assertEquals("print(\"Hello\")", request.firstValue.code)

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_RUN)
        assertElementPresent("#exec")
        assertElementNotPresent("#exec .error")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(
            statusCode = 409,
            errorCode = ErrorCode.SCRIPT_EXECUTION_FAILED,
            message = "Failed",
            data = mapOf(
                "console" to ">> Hello world"
            )
        )
        doThrow(ex).whenever(kokiScripts).run(any())

        navigateTo("/settings/scripts/${script.id}/run")

        scrollToBottom()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_RUN)
        assertElementPresent("#exec")
        assertElementPresent("#exec .error")
    }

    @Test
    fun cancel() {
        navigateTo("/settings/scripts/${script.id}/run")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
    }

    @Test
    fun update() {
        navigateTo("/settings/scripts/${script.id}/run")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_RUN)

        assertElementNotPresent("#exec")
        input("textarea[name=parameters]", "var1=100\nvar3=1\n\n")
        select("select[name=language]", 1)
        inputCodeMiror("print(\"Hello\")")
        scrollToBottom()
        click(".btn-update")

        Thread.sleep(1000)
        val request = argumentCaptor<UpdateScriptRequest>()
        verify(kokiScripts).update(eq(script.id), request.capture())

        assertEquals(script.name, request.firstValue.name)
        assertEquals(script.title, request.firstValue.title)
        assertEquals(script.description, request.firstValue.description)
        assertEquals(listOf("var1", "var3"), request.firstValue.parameters)
        assertEquals(Language.PYTHON, request.firstValue.language)
//        assertEquals("print(\"Hello\")", request.firstValue.code)
        assertEquals(script.active, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
    }
}
