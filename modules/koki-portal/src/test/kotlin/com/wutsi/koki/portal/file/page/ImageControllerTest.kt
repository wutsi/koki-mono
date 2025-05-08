package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ImageControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                GetFileResponse(image),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )
    }

    @Test
    fun show() {
        navigateTo("/images/${image.id}?owner-id=111&owner-type=ROOM")

        assertCurrentPageIs(PageName.IMAGE)
    }

    @Test
    fun delete() {
        navigateTo("/images/${image.id}?owner-id=111&owner-type=ROOM")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/files/${image.id}")

        assertCurrentPageIs(PageName.ROOM)
    }
}
