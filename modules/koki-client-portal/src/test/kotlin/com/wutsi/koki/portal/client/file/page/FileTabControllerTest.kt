package com.wutsi.koki.portal.client.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.FileFixtures.files
import com.wutsi.koki.portal.client.common.page.PageName
import kotlin.test.Test

class FileTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/files/tab?owner-id=111&owner-type=TAX&test-mode=true")

        assertElementPresent(".dropzone")
        assertElementCount(".tab-files tr.file", files.size)
    }

    @Test
    fun refresh() {
        navigateTo("/files/tab?owner-id=111&owner-type=TAX&test-mode=true")

        click(".btn-refresh")

        verify(rest, times(2)).getForEntity(
            any<String>(),
            eq(SearchFileResponse::class.java)
        )
    }

    @Test
    fun delete() {
        navigateTo("/files/tab?owner-id=111&owner-type=TAX&test-mode=true")
        assertElementPresent("#file-" + files[1].id)

        click("#file-" + files[1].id + " .btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/files/" + files[1].id)

        Thread.sleep(1000)
        assertElementNotPresent("#file-" + files[1].id)
    }

    @Test
    fun `no access to module`() {
        disableModule("file")

        navigateTo("/files/tab?owner-id=111&owner-type=TAX&test-mode=true")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/files/tab?owner-id=111&owner-type=TAX&test-mode=true")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
