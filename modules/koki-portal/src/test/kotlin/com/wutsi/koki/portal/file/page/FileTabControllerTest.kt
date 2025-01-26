package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class FileTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementCount(".tab-files tr.file", files.size)
        assertElementAttribute("#file-list", "data-owner-id", "111")
        assertElementAttribute("#file-list", "data-owner-type", "ACCOUNT")
    }

    @Test
    fun upload() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        click(".btn-upload", 1000)

        assertElementVisible("#koki-modal")
    }

    @Test
    fun download() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        click(".btn-download")
    }

    @Test
    fun delete() {
        navigateTo("/files/tab?test-mode=true&owner-id=555&owner-type=ACCOUNT")

        val id = file.id
        click("#file-$id .btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/files/$id")
    }

    @Test
    fun email() {
        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        click("tr .btn-email", 1000)

        assertElementVisible("#koki-modal")
        assertElementVisible("#attachment-${file.id}")
    }

    @Test
    fun `list - without permission file-manage`() {
        setUpUserWithoutPermissions(listOf("file:manage"))

        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-upload")
    }

    @Test
    fun `list - without permission email-send`() {
        setUpUserWithoutPermissions(listOf("email:send"))

        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-email")
        assertElementPresent(".btn-upload")
    }

    @Test
    fun `list - without permission file`() {
        setUpUserWithoutPermissions(listOf("file"))

        navigateTo("/files/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
