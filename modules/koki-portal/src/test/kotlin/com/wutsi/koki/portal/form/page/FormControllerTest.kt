package com.wutsi.koki.portal.form.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.FormFixtures.form
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FormControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/forms/${form.id}")
        assertCurrentPageIs(PageName.FORM)
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-create")
        assertElementPresent(".btn-delete")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/forms/${form.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission form`() {
        setUpUserWithoutPermissions(listOf("form"))

        navigateTo("/forms/${form.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission form-manage`() {
        setUpUserWithoutPermissions(listOf("form:manage"))

        navigateTo("/forms/${form.id}")
        assertCurrentPageIs(PageName.FORM)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
        assertElementPresent(".btn-delete")
    }

    @Test
    fun `show - without permission form-delete`() {
        setUpUserWithoutPermissions(listOf("form:delete"))

        navigateTo("/forms/${form.id}")
        assertCurrentPageIs(PageName.FORM)
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-create")
        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun files() {
        navigateTo("/forms/${form.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files .file", FileFixtures.files.size)
    }

    @Test
    fun delete() {
        navigateTo("/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/forms/${form.id}")
        assertCurrentPageIs(PageName.FORM_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete - dismiss`() {
        navigateTo("/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun `delete - error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.FORM)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun create() {
        navigateTo("/forms/${form.id}")
        click(".btn-create")
        assertCurrentPageIs(PageName.FORM_CREATE)
    }

    @Test
    fun edit() {
        navigateTo("/forms/${form.id}")
        click(".btn-edit")
        assertCurrentPageIs(PageName.FORM_EDIT)
    }
}
