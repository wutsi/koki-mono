package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.FileFixtures.files
import kotlin.test.Test

class ListFileWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/files/widgets/list?test-mode=true")

        assertElementCount(".widget-files tr.file", files.size)
    }

    @Test
    fun upload() {
        navigateTo("/files/widgets/list?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        click(".btn-upload", 1000)

        assertElementVisible("#file-modal")
    }

    @Test
    fun download() {
        navigateTo("/files/widgets/list?test-mode=true")

        click(".btn-download")
    }

    @Test
    fun delete() {
        navigateTo("/files/widgets/list?test-mode=true&owner-id=555&owner-type=ACCOUNT")

        val id = file.id
        click("#file-$id .btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(kokiFiles).delete(id)
    }
}
