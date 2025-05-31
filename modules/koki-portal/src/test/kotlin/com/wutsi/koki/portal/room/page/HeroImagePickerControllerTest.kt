package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.FileFixtures.images
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.SetHeroImageRequest
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class HeroImagePickerControllerTest : AbstractPageControllerTest() {
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
    fun pick() {
        navigateTo("/rooms/${room.id}/hero-image-picker")
        assertCurrentPageIs(PageName.ROOM_HERO_IMAGE)

        assertElementCount("img.hero-image", images.size - 1)

        val imageId = images.find { img -> img.id != room.heroImageId }!!.id
        click("#image-$imageId .btn-select")

        verify(rest).postForEntity(
            "$sdkBaseUrl/v1/rooms/${room.id}/hero-image",
            SetHeroImageRequest(imageId),
            Any::class.java
        )

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `show - without permission room`() {
        setUpUserWithoutPermissions(listOf("room"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - without permission room-manage`() {
        setUpUserWithoutPermissions(listOf("room:manage"))

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
