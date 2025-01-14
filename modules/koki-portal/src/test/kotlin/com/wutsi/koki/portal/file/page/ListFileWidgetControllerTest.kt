package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.file.dto.SearchFileResponse
import kotlin.test.Test

class ListFileWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/files/widgets/list?test-mode=true")

        assertElementCount(".widget-files tr.file", files.size)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun empty() {
        doReturn(SearchFileResponse()).whenever(kokiFiles)
            .files(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/files/widgets/list?test-mode=true")

        assertElementNotPresent(".widget-files tr.file")
        assertElementPresent(".widget-files .empty-message")
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
