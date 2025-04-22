package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class FileControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT")

        assertCurrentPageIs(PageName.FILE)
    }

    @Test
    fun readOnly() {
        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT&read-only=true")

        assertCurrentPageIs(PageName.FILE)
        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun download() {
        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT")

        click(".btn-download")
    }

    @Test
    fun delete() {
        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT")

        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/files/${file.id}")

        assertCurrentPageIs(PageName.ACCOUNT)
    }

    @Test
    fun `show - without permission file-delete`() {
        setUpUserWithoutPermissions(listOf("file:delete"))

        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT")

        assertElementNotPresent(".btn-delete")
    }
}
