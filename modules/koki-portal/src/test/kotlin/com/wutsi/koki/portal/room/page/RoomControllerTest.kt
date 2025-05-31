package com.wutsi.koki.portal.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.FileFixtures.images
import com.wutsi.koki.RoomFixtures.room
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class RoomControllerTest : AbstractPageControllerTest() {
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
    fun show() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        assertElementAttribute("img.hero-image", "src", image.url)

        assertElementAttribute("[data-component-id=map]", "data-latitude", room.latitude.toString())
        assertElementAttribute("[data-component-id=map]", "data-longitude", room.longitude.toString())
        assertElementAttribute("[data-component-id=map]", "data-show-marker", "true")

        assertElementNotPresent("#listing-container")
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-map")
        assertElementPresent(".btn-publish")
        assertElementPresent(".btn-delete")
        assertElementPresent(".btn-clone")
    }

    @Test
    fun publishing() {
        doReturn(
            ResponseEntity(
                GetRoomResponse(room.copy(status = RoomStatus.PUBLISHING)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        assertElementNotPresent("#listing-container")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-publish")
        assertElementNotPresent(".btn-delete")
        assertElementNotPresent(".btn-map")
        assertElementNotPresent(".btn-clone")
    }

    @Test
    fun published() {
        doReturn(
            ResponseEntity(
                GetRoomResponse(room.copy(status = RoomStatus.PUBLISHED)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        assertElementPresent("#listing-container")
        assertElementAttributeEndsWith("#listing-container a", "href", room.listingUrl ?: "")
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-map")
        assertElementNotPresent(".btn-publish")
        assertElementNotPresent(".btn-delete")
        assertElementPresent(".btn-clone")
    }

    @Test
    fun delete() {
        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete - dismiss`() {
        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `delete - error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/rooms/${room.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.ROOM)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/rooms/${room.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.ROOM_EDIT)
    }

    @Test
    fun clone() {
        navigateTo("/rooms/${room.id}")
        click(".btn-clone")

        assertCurrentPageIs(PageName.ROOM_CREATE)
    }

    @Test
    fun map() {
        navigateTo("/rooms/${room.id}")
        scrollToBottom()
        click(".btn-map")

        assertCurrentPageIs(PageName.ROOM_MAP)
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

        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-map")
        assertElementNotPresent(".btn-publish")
        assertElementNotPresent(".btn-delete")
        assertElementNotPresent(".btn-clone")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
