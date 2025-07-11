package com.wutsi.koki.room.web.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.web.AbstractPageControllerTest
import com.wutsi.koki.room.web.FileFixtures.images
import com.wutsi.koki.room.web.RefDataFixtures.cities
import com.wutsi.koki.room.web.RefDataFixtures.countries
import com.wutsi.koki.room.web.RefDataFixtures.neighborhoods
import com.wutsi.koki.room.web.RoomFixtures.rooms
import com.wutsi.koki.room.web.TenantFixtures
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.room.web.geoip.model.GeoIpModel
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.openqa.selenium.By.id
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
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
    fun `show - resolved from geo-ip`() {
        // GIVEN
        val city = cities[1]
        doReturn(
            ResponseEntity(
                SearchLocationResponse(listOf(city)),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchLocationResponse::class.java)
            )

        // WHEN
        navigateTo("/l")

        // THEN
        assertCurrentPageIs(PageName.LOCATION)
        assertEquals("http://localhost:$port" + StringUtils.toSlug("/l/${city.id}", city.name), driver.currentUrl)
    }

    @Test
    fun `show - no geo-ip, fallback to popular city`() {
        // GIVEN
        doThrow(HttpClientErrorException(HttpStatusCode.valueOf(404)))
            .whenever(rest).getForEntity("https://ipapi.co/json", GeoIpModel::class.java)

        // WHEN
        navigateTo("/l")

        // THEN
        assertEquals(true, driver.currentUrl?.startsWith("http://localhost:$port/l/"))
    }

    @Test
    fun `show - no geo-ip, no popular city`() {
        doReturn(
            ResponseEntity(
                SearchLocationResponse(emptyList<Location>()),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchLocationResponse::class.java)
            )

        // WHEN
        navigateTo("/l")

        // THEN
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `list - neighborhood`() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(neighborhoods[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        navigateTo("/l/${neighborhoods[0].id}/centre-ville")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount("div.room", rooms.size)
        assertElementNotPresent(".room-list-empty")

        // Opengraph
        assertElementAttribute("html", "lang", "fr")
        assertEquals("${neighborhoods[0].name} à Louer", driver.title)
        assertElementPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", "${neighborhoods[0].name} à Louer")
        assertElementPresent("head meta[property='og:description']")
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0/l/${neighborhoods[0].id}/centre-ville"
        )
        assertElementNotPresent("head meta[property='og:image']")

        // Property Map
        assertElementAttribute("#map", "data-longitude", neighborhoods[0].longitude.toString())
        assertElementAttribute("#map", "data-latitude", neighborhoods[0].latitude.toString())
        assertElementAttribute("#map", "data-show-marker", "false")
        assertElementAttribute("#map", "data-zoom", "16")

        // Access a room
        click(".room img")

        val windowHandles = driver.getWindowHandles().toList()
        assertEquals(2, windowHandles.size)
        driver.switchTo().window(windowHandles[1])

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `list - city`() {
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

        navigateTo("/l/${cities[0].id}/montreal")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount("div.room", rooms.size)
        assertElementNotPresent(".room-list-empty")

        // Opengraph
        assertElementAttribute("html", "lang", "fr")
        assertEquals("${cities[0].name} à Louer", driver.title)
        assertElementPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", "${cities[0].name} à Louer")
        assertElementPresent("head meta[property='og:description']")
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0/l/${cities[0].id}/montreal"
        )
        assertElementNotPresent("head meta[property='og:image']")

        // Property Map
        assertElementAttribute("#map", "data-longitude", cities[0].longitude.toString())
        assertElementAttribute("#map", "data-latitude", cities[0].latitude.toString())
        assertElementAttribute("#map", "data-show-marker", "false")
        assertElementAttribute("#map", "data-zoom", "14")

        // Access a room
        click(".room img")

        val windowHandles = driver.getWindowHandles().toList()
        assertEquals(2, windowHandles.size)
        driver.switchTo().window(windowHandles[1])

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `list - country`() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(countries[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        navigateTo("/l/${countries[0].id}/canada")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `list - english`() {
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

        navigateTo("/l/${cities[0].id}/montreal?lang=en")

        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head meta[property='og:title']", "content", "${cities[0].name} Rentals")

        assertCurrentPageIs(PageName.LOCATION)
    }

    @Test
    fun `list - empty`() {
        doReturn(
            ResponseEntity(
                SearchRoomResponse(emptyList<RoomSummary>()),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoomResponse::class.java)
            )

        navigateTo("/l/${neighborhoods[0].id}/canada")

        assertElementPresent(".room-list-empty")
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

        navigateTo("/l/${neighborhoods[0].id}/montreal")

        assertCurrentPageIs(PageName.LOCATION)
        assertElementCount("div.room", entries.size)

        scrollToBottom()
        click("#room-load-more button", 1000)
        assertElementCount("div.room", entries.size + rooms.size)
    }

    @Test
    fun map() {
        navigateTo("/l/${neighborhoods[0].id}/montreal")

        Thread.sleep(2000)
        val markers = rooms.filter { room -> room.latitude != null && room.longitude != null }
        assertElementCount(".map-room-icon", markers.size)

        // Open card
        click(".map-room-icon")
        assertElementVisible(".map-room-card")

        // Click
        click(".map-room-card img")

        val windowHandles = driver.getWindowHandles().toList()
        assertEquals(2, windowHandles.size)
        driver.switchTo().window(windowHandles[1])

        assertCurrentPageIs(PageName.ROOM)
    }

    @Test
    fun `map - IMPRESSION when opening card`() {
        navigateTo("/l/${neighborhoods[0].id}/montreal")
        Thread.sleep(2000)

        click(".map-room-icon")

        reset(publisher)
        click(".map-room-card img")

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, times(2)).publish(event.capture()) // CLICK then VIEW

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals("map", event.firstValue.track.component)
        assertEquals(TrackEvent.CLICK, event.firstValue.track.event)
        assertEquals(false, event.firstValue.track.productId.isNullOrEmpty())
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(-1, event.firstValue.track.rank)
    }

    @Test
    fun `map - CLICK when click a card`() {
        navigateTo("/l/${neighborhoods[0].id}/montreal")
        Thread.sleep(2000)

        reset(publisher)
        click(".map-room-icon")

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals("map", event.firstValue.track.component)
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals(false, event.firstValue.track.productId.isNullOrEmpty())
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(-1, event.firstValue.track.rank)
    }

    @Test
    fun `IMPRESSION tracking on page load`() {
        val ids = rooms.map { room -> room.id.toString() }.joinToString("|")

        navigateTo("/l/${neighborhoods[0].id}/montreal")
        Thread.sleep(1000)

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals(ids, event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
    }

    @Test
    fun `IMPRESSION tracking when clicking on a map marker`() {
        navigateTo("/l/${neighborhoods[0].id}/montreal")
        Thread.sleep(2000)

        verify(publisher).publish(any()) // IMPRESSION of the list
        reset(publisher)

        click(".map-room-icon")

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals("map", event.firstValue.track.component)
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals(rooms[1].id.toString(), event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(-1, event.firstValue.track.rank)
    }

    @Test
    fun `IMPRESSION tracking on load more`() {
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

        navigateTo("/l/${neighborhoods[0].id}/montreal")
        scrollToBottom()
        reset(publisher)
        click("#room-load-more button")

        val productIds = rooms.map { room -> room.id }.joinToString("|")

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.IMPRESSION, event.firstValue.track.event)
        assertEquals(productIds, event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
    }

    @Test
    fun `CLICK tracking when clicking on Room`() {
        navigateTo("/l/${neighborhoods[0].id}/montreal")
        Thread.sleep(1000)
        reset(publisher)

        click(".room:nth-child(2)")
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, times(2)).publish(event.capture()) // 1st event=CLICK, 2nd event=VIEW

        assertEquals(PageName.LOCATION, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.CLICK, event.firstValue.track.event)
        assertEquals(rooms[1].id.toString(), event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/l/${neighborhoods[0].id}/montreal", event.firstValue.track.url)
        assertEquals(1, event.firstValue.track.rank)
    }

    @Test
    fun filter() {
        navigateTo("/l/${neighborhoods[0].id}/centre-ville")
        click("#btn-filter")

        assertElementVisible("#filter-modal")
        click("#room-type-apartment-label")
        click("#bedrooms-2-label")
        click("#furnished-type-none-label")
        click("#lease-type-short-label")
        click("#btn-apply-filter")

        assertElementNotVisible("#filter-modal")
        assertCurrentPageIs(PageName.LOCATION)

        assertElementPresent("#filter-badge-APARTMENT")
        assertElementPresent("#filter-badge-bedroom-2")
        assertElementPresent("#filter-badge-SHORT_TERM")
        assertElementPresent("#filter-badge-NONE")
    }
}
