package com.wutsi.koki.room.web.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.web.AbstractPageControllerTest
import com.wutsi.koki.room.web.FileFixtures.images
import com.wutsi.koki.room.web.RoomFixtures.room
import com.wutsi.koki.room.web.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomControllerTest : AbstractPageControllerTest() {
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
    fun show() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        // Opengraph
        assertElementAttribute("html", "lang", "en")
        assertEquals(room.title, driver.title)
        assertElementAttribute("head meta[name='description']", "content", room.summary)

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", room.title)
        assertElementAttribute("head meta[property='og:description']", "content", room.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0/rooms/${room.id}/windmill-in-ponta-delgada"
        )
        assertElementAttribute("head meta[property='og:image']", "content", images[0].url)

        // Property infos
        assertElementText("h1#room-title", room.title)
        assertElementText("#room-summary", room.summary)
        assertElementPresent("#room-price")
        assertElementPresent("#btn-contact-us")

        // Property Map
        assertElementAttribute("#room-map", "data-longitude", room.longitude.toString())
        assertElementAttribute("#room-map", "data-latitude", room.latitude.toString())
        assertElementAttribute("#room-map", "data-show-marker", "true")
        assertElementAttribute("#room-map", "data-zoom", "18")
    }

    @Test
    fun `show description`() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        click("#btn-description")

        assertElementVisible("#room-description-modal")
        assertElementText("#room-description-modal .modal-body", room.description)
        click("#room-description-modal .btn-close")
    }

    @Test
    fun `show amenities`() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        scroll(.25)
        click("#btn-amenities")

        assertElementVisible("#room-amenities-modal")
        assertElementCount("#room-amenities-modal .modal-body li.amenity", room.amenityIds.size)
        click("#room-amenities-modal .btn-close")
    }

    @Test
    fun `show images`() {
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ROOM)

        scroll(.25)
        click("#btn-images")

        assertElementVisible("#room-images-modal")
        assertElementCount("#room-images-modal .modal-body img", images.size)
        click("#room-images-modal .btn-close")
    }

    @Test
    fun `show - status=DRAFT`() {
        setupRoomStatus(RoomStatus.DRAFT)
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `show - status=PUBLISHING`() {
        setupRoomStatus(RoomStatus.PUBLISHING)
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `show - status=EXPIRED`() {
        setupRoomStatus(RoomStatus.EXPIRED)
        navigateTo("/rooms/${room.id}")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    private fun setupRoomStatus(status: RoomStatus) {
        doReturn(
            ResponseEntity(
                GetRoomResponse(room.copy(status = status)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetRoomResponse::class.java)
            )
    }
}
