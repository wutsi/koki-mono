package com.wutsi.koki.room.web.room.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.web.AbstractPageControllerTest
import com.wutsi.koki.room.web.FileFixtures.images
import com.wutsi.koki.room.web.GeoIpFixtures
import com.wutsi.koki.room.web.MessageFixtures
import com.wutsi.koki.room.web.RefDataFixtures
import com.wutsi.koki.room.web.RoomFixtures.room
import com.wutsi.koki.room.web.TenantFixtures
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
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

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertEquals(room.title, driver.title)
        assertElementAttribute("head meta[name='description']", "content", room.summary)

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", room.title)
        assertElementAttribute("head meta[property='og:description']", "content", room.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:url']",
            "content",
            "http://localhost:0${room.listingUrl}"
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
    fun `show - english translation`() {
        navigateTo("/rooms/${room.id}?lang=en")
        assertCurrentPageIs(PageName.ROOM)

        assertElementAttribute("html", "lang", "en")
    }

    @Test
    fun `show description`() {
        navigateTo("/rooms/${room.id}")

        click("#btn-description")

        assertElementVisible("#room-description-modal")
        assertElementText("#room-description-modal .modal-body", room.description)
        click("#room-description-modal .btn-close")
    }

    @Test
    fun `show amenities`() {
        navigateTo("/rooms/${room.id}")

        scroll(.25)
        click("#btn-amenities")

        assertElementVisible("#room-amenities-modal")
        assertElementCount("#room-amenities-modal .modal-body li.amenity", room.amenityIds.size)
        click("#room-amenities-modal .btn-close")
    }

    @Test
    fun `show images`() {
        navigateTo("/rooms/${room.id}")

        click("#btn-images")

        assertElementVisible("#room-images-modal")
        assertElementCount("#room-images-modal .modal-body img", images.size)
        click("#room-images-modal .btn-close")
    }

    @Test
    fun `VIEW tracking on page load`() {
        navigateTo("/rooms/${room.id}/this-is-room")

        Thread.sleep(1000)
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.ROOM, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.VIEW, event.firstValue.track.event)
        assertEquals(room.id.toString(), event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/rooms/${room.id}/this-is-room", event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
    }

    @Test
    fun `send message`() {
        navigateTo("/rooms/${room.id}")

        assertElementNotVisible("#room-message-modal")
        assertElementAttribute("#phone", "data-country", GeoIpFixtures.geoip.countryCode)
        reset(publisher)
        click("#btn-contact-us")

        assertElementVisible("#room-message-modal")
        input("#name", "Ray Sponsible")
        input("#email", "ray.spomsible@gmail.com")
        input("#phone", "514 758 0001")
        input("#body", "This is a nice message... I Love it :-)")
        click("#btn-send")

        val request = argumentCaptor<SendMessageRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages"),
            request.capture(),
            eq(SendMessageResponse::class.java)
        )
        assertEquals(room.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.ROOM, request.firstValue.owner?.type)
        assertEquals("Ray Sponsible", request.firstValue.senderName)
        assertEquals("ray.spomsible@gmail.com", request.firstValue.senderEmail)
        assertEquals("+15147580001", request.firstValue.senderPhone)
        assertEquals("This is a nice message... I Love it :-)", request.firstValue.body)
        assertEquals("fr", request.firstValue.language)
        assertEquals(GeoIpFixtures.geoip.countryCode, request.firstValue.country)
        assertEquals(RefDataFixtures.locations[0].id, request.firstValue.cityId)

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        assertElementNotVisible("#room-message-modal")

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(PageName.ROOM, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.MESSAGE, event.firstValue.track.event)
        assertEquals(room.id.toString(), event.firstValue.track.productId)
        assertEquals(MessageFixtures.NEW_ID.toString(), event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertEquals("http://localhost:$port/rooms/${room.id}", event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
    }

    @Test
    fun `send message with missing fields`() {
        navigateTo("/rooms/${room.id}")

        click("#btn-contact-us")

        assertElementVisible("#room-message-modal")
        click("#btn-send")

        verify(rest, never()).postForEntity(
            any<String>(),
            any<SendMessageRequest>(),
            eq(Any::class.java)
        )

        assertElementVisible("#room-message-modal")
    }

    @Test
    fun `send message with error`() {
        val ex = createHttpClientErrorException(409, errorCode = ErrorCode.HTTP_DOWNSTREAM_ERROR)
        doThrow(ex).whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/messages"),
                any<SendMessageRequest>(),
                eq(SendMessageResponse::class.java)
            )

        navigateTo("/rooms/${room.id}")
        click("#btn-contact-us")

        assertElementVisible("#room-message-modal")
        input("#name", "Ray Sponsible")
        input("#email", "ray.spomsible@gmail.com")
        input("#phone", "514 758 0001")
        input("#body", "This is a nice message... I Love it :-)")
        click("#btn-send")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertElementVisible("#room-message-modal")
    }

    @Test
    fun `click on neighborhood link`() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(RefDataFixtures.neighborhoods[0]),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                GetLocationResponse(RefDataFixtures.cities[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        navigateTo("/rooms/${room.id}")

        click(".lnk-neighborhood")
        assertCurrentPageIs(PageName.LOCATION)
    }

    @Test
    fun `click on city link`() {
        doReturn(
            ResponseEntity(
                GetLocationResponse(RefDataFixtures.cities[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )

        navigateTo("/rooms/${room.id}")

        click(".lnk-city")
        assertCurrentPageIs(PageName.LOCATION)
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
