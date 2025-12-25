package com.wutsi.koki.portal.user.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.UpdateUserPhotoRequest
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class UserPhotoControllerTest : AbstractPageControllerTest() {
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
    fun upload() {
        navigateTo("/users/photo")

        assertCurrentPageIs(PageName.USER_PHOTO)

        input("#file-upload", getImageFile().absolutePath)
        Thread.sleep(5000)

        val request3 = argumentCaptor<UpdateUserPhotoRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}/photo"),
            request3.capture(),
            eq(Any::class.java),
        )
        assertEquals(image.url, request3.firstValue.photoUrl)

        assertCurrentPageIs(PageName.HOME)
    }

    private fun getImageFile(): File {
        val file = File.createTempFile("photo", ".png")
        file.deleteOnExit()

        val input = this::class.java.getResourceAsStream("/photo.png")
        val output = FileOutputStream(file)
        output.use {
            IOUtils.copy(input, output)
        }
        return file
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()
        navigateTo("/users/photo")

        assertCurrentPageIs(PageName.LOGIN)
    }
}
