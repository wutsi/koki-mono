package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.FileFixtures.file
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun `delete file`() {
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
    fun `delete image`() {
        doReturn(
            ResponseEntity(
                GetFileResponse(FileFixtures.image),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )

        navigateTo("/files/${FileFixtures.image.id}?owner-id=555&owner-type=ROOM")

        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/files/${file.id}")

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `show - without permission file-delete`() {
        setupUserWithoutPermissions(listOf("file:delete"))

        navigateTo("/files/${file.id}?owner-id=555&owner-type=ACCOUNT")

        assertElementNotPresent(".btn-delete")
    }
}
