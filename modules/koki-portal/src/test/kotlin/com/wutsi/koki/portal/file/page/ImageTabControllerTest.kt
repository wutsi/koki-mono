package com.wutsi.koki.portal.file.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
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
        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT")

        assertElementCount(".tab-images tr.image", images.size)
        assertElementPresent(".btn-refresh")
        assertElementPresent(".uploader")
    }

    @Test
    fun `read-only`() {
        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true&read-only=true")

        assertElementCount(".tab-images tr.image", images.size)
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".btn-upload")
    }

    @Test
    fun `list - without permission image-manage`() {
        setUpUserWithoutPermissions(listOf("image:manage"))

        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        assertElementPresent(".btn-refresh")
        assertElementNotPresent(".uploader")
    }

    @Test
    fun `list - without permission image`() {
        setUpUserWithoutPermissions(listOf("image"))

        navigateTo("/images/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
