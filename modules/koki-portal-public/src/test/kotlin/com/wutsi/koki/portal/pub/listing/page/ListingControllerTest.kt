package com.wutsi.koki.portal.pub.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.FileFixtures
import com.wutsi.koki.portal.pub.ListingFixtures.listing
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ListingControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                SearchFileResponse(FileFixtures.images),
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
        navigateTo("/listings${listing.publicUrl}")
        assertCurrentPageIs(PageName.LISTING)

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute("head meta[name='description']", "content", listing.summaryFr)

        // Opengraph
        assertElementAttributePresent("head meta[property='og:title']", "content")
        assertElementAttribute("head meta[property='og:description']", "content", listing.summaryFr)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:0${listing.publicUrlFr}")
        assertElementAttribute("head meta[property='og:image']", "content", FileFixtures.images[0].url)

        // Property infos
        assertElementText("#listing-title", listing.titleFr)
        assertElementText("#listing-description", listing.descriptionFr)
        assertElementPresent("#listing-price")

        // Property Map
        assertElementAttribute("#listing-map", "data-longitude", listing.geoLocation?.longitude?.toString())
        assertElementAttribute("#listing-map", "data-latitude", listing.geoLocation?.latitude?.toString())
        assertElementAttribute("#listing-map", "data-show-marker", "true")
        assertElementAttribute("#listing-map", "data-zoom", "18")
    }

    @Test
    fun `show - english translation`() {
        navigateTo("/listings/${listing.id}?lang=en")
        assertCurrentPageIs(PageName.LISTING)

        assertElementAttribute("html", "lang", "en")
    }

    @Test
    fun `show amenities`() {
        navigateTo("/listings/${listing.id}")

        scroll(.25)
        click("#btn-amenities")

        assertElementVisible("#listing-amenities-modal")
        assertElementCount("#listing-amenities-modal .modal-body li.amenity", listing.amenityIds.size)
        click("#listing-amenities-modal .btn-close")
    }

    @Test
    fun `show images`() {
        navigateTo("/listings/${listing.id}")

        click("#btn-images")

        assertElementVisible("#listing-images-modal")
        assertElementCount("#listing-images-modal .modal-body img", FileFixtures.images.size)
        click("#listing-images-modal .btn-close")
    }

    @Test
    fun `VIEW tracking on page load`() {
        navigateTo("/listings/${listing.id}")

        Thread.sleep(1000)
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LISTING, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.VIEW, event.firstValue.track.event)
        assertEquals(listing.id.toString(), event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertNotNull(event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
    }
}
