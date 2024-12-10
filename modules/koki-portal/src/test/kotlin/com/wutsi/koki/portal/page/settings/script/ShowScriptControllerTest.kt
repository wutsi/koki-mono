package com.wutsi.koki.portal.page.settings.script

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ScriptFixtures.script
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ShowScriptControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/scripts/${script.id}")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)

        assertElementNotPresent(".label-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/scripts/${script.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/settings/scripts/${script.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiScripts).delete(script.id)
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)
    }

    @Test
    fun `delete dismiss`() {
        navigateTo("/settings/scripts/${script.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.SCRIPT_IN_USE)
        doThrow(ex).whenever(kokiScripts).delete(any())

        navigateTo("/settings/scripts/${script.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/settings/scripts/${script.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_EDIT)
    }
}
