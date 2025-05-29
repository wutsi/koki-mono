package com.wutsi.koki.room.web.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.web.AbstractPageControllerTest
import com.wutsi.koki.room.web.FileFixtures.images
import com.wutsi.koki.room.web.RefDataFixtures.cities
import com.wutsi.koki.room.web.RefDataFixtures.neighborhoods
import com.wutsi.koki.room.web.RoomFixtures.room
import com.wutsi.koki.room.web.RoomFixtures.rooms
import com.wutsi.koki.room.web.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationControllerTest : AbstractPageControllerTest() {
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

        doReturn(
            ResponseEntity(
                GetLocationResponse(neighborhoods[0]),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                GetLocationResponse(cities[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )
    }

    @Test
    fun neighborhood() {
        navigateTo("/locations/${neighborhoods[0].id}/ffo-bar")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount(".room", rooms.size)

        // Opengraph
        assertElementAttribute("html", "lang", "en")
        assertEquals("${neighborhoods[0].name} Rentals", driver.title)
        assertElementPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", "${neighborhoods[0].name} Rentals")
        assertElementPresent("head meta[property='og:description']")
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0/locations/${neighborhoods[0].id}/ffo-bar"
        )
        assertElementNotPresent("head meta[property='og:image']")

        // Property Map
        assertElementAttribute("#map", "data-longitude", neighborhoods[0].longitude.toString())
        assertElementAttribute("#map", "data-latitude", neighborhoods[0].latitude.toString())
        assertElementAttribute("#map", "data-show-marker", "false")
        assertElementAttribute("#map", "data-zoom", "16")
    }

    @Test
    fun city() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(cities[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        navigateTo("/locations/${cities[0].id}/ffo-bar")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount(".room", rooms.size)

        // Opengraph
        assertElementAttribute("html", "lang", "en")
        assertEquals("${cities[0].name} Rentals", driver.title)
        assertElementPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", "${cities[0].name} Rentals")
        assertElementPresent("head meta[property='og:description']")
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0/locations/${cities[0].id}/ffo-bar"
        )
        assertElementNotPresent("head meta[property='og:image']")

        // Property Map
        assertElementAttribute("#map", "data-longitude", cities[0].longitude.toString())
        assertElementAttribute("#map", "data-latitude", cities[0].latitude.toString())
        assertElementAttribute("#map", "data-show-marker", "false")
        assertElementAttribute("#map", "data-zoom", "14")
    }

    @Test
    fun `load more`() {
        var entries = mutableListOf<RoomSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(rooms[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchRoomResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchRoomResponse(rooms),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomResponse::class.java)
            )

        navigateTo("/locations/${neighborhoods[0].id}/ffo-bar")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount(".room", entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount(".room", entries.size + rooms.size)
    }
}
