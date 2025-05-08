package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.images
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ImageTabControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                SearchFileResponse(images),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFileResponse::class.java)
            )
    }

    @Test
    fun list() {
        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementCount(".tab-images tr.image", images.size)
        assertElementPresent(".btn-refresh")
        assertElementPresent(".uploader")
        assertElementPresent(".btn-delete")
    }

    @Test
    fun `read-only`() {
        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true&read-only=true")

        assertElementCount(".tab-images tr.image", images.size)
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".btn-upload")
        assertElementNotPresent(".btn-delete")
    }

//    @Test
//    fun upload() {
//        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
//        click(".btn-upload", 1000)
//
//        assertElementVisible("#koki-modal")
//    }

    @Test
    fun `list - without permission image-manage`() {
        setUpUserWithoutPermissions(listOf("image:manage"))

        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".uploader")
        assertElementNotPresent(".btn-delete")
    }

    @Test
    fun `list - without permission image`() {
        setUpUserWithoutPermissions(listOf("image"))

        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun delete() {
        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/files/${images[0].id}")
    }
}
