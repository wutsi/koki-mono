package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class FileControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/files/${file.id}")

        assertElementNotPresent(".btn-return")
        assertCurrentPageIs(PageName.FILE)
    }

    @Test
    fun `show with return-url`() {
        navigateTo("/files/${file.id}?return-url=/")

        assertElementPresent(".btn-return")
        click(".btn-return")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun download() {
        navigateTo("/files/${file.id}")

        click(".btn-download")
    }

    @Test
    fun delete() {
        navigateTo("/files/${file.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiFiles).delete(file.id)
        assertCurrentPageIs(PageName.FILE_DELETED)

        assertElementNotPresent(".btn-return")
    }

    @Test
    fun `delete with return-url`() {
        navigateTo("/files/${file.id}?return-url=/")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiFiles).delete(file.id)
        assertCurrentPageIs(PageName.FILE_DELETED)

        assertElementPresent(".btn-return")
        click(".btn-return")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `delete and dismiss`() {
        navigateTo("/files/${file.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiFiles, never()).delete(any())
        assertCurrentPageIs(PageName.FILE)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiFiles).delete(any())

        navigateTo("/files/${file.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.FILE)
        assertElementPresent(".alert-danger")
    }
}
