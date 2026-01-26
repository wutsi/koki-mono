package com.wutsi.koki.portal.pub.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.SearchSimilarListingResponse
import com.wutsi.koki.place.dto.SearchPlaceResponse
import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.FileFixtures
import com.wutsi.koki.portal.pub.ListingFixtures.listing
import com.wutsi.koki.portal.pub.ListingFixtures.similar
import com.wutsi.koki.portal.pub.RefDataFixtures.cities
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.user.service.UserIdProvider
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ListingControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var ipService: GeoIpService

    @MockitoBean
    private lateinit var userIdProvider: UserIdProvider

    private val geoIp = GeoIp(
        countryCode = "CA",
        city = cities[0].name,
    )

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

        doReturn(geoIp).whenever(ipService).resolve(any())

        doReturn(null).whenever(userIdProvider).get()
    }

    @Test
    fun show() {
        navigateTo(listing.publicUrl!!)
        assertCurrentPageIs(PageName.LISTING)

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute("head meta[name='description']", "content", listing.summaryFr)

        // Opengraph
        assertElementAttributeContains("head meta[property='og:title']", "content", (listing.titleFr ?: ""))
        assertElementAttribute("head meta[property='og:description']", "content", listing.summaryFr)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", (listing.publicUrlFr ?: ""))
        assertElementAttribute("head meta[property='og:image']", "content", FileFixtures.images[0].url)
        assertElementAttribute("head meta[property='og:updated_time']", "content", listing.publishedAt?.time.toString())

        // Property infos
        assertElementText("#listing-title", listing.titleFr)
        assertElementText("#listing-description", listing.descriptionFr)
        assertElementPresent("#listing-price")

        // Property Map
        assertElementAttribute("[data-component-id=map]", "data-longitude", listing.geoLocation?.longitude?.toString())
        assertElementAttribute("[data-component-id=map]", "data-latitude", listing.geoLocation?.latitude?.toString())
        assertElementAttribute("[data-component-id=map]", "data-show-marker", "true")
        assertElementAttribute("[data-component-id=map]", "data-zoom", "18")

        // Content
        assertElementPresent("#btn-share-navbar")
        assertElementNotPresent(".listing-status") // Status badge - only for sold listings
        assertElementCount("#similar-listing-container .listing-card", similar.size) // Similar listings
        assertElementPresent("#description-container")
        assertElementPresent("#legal-container")
        assertElementPresent("#amenity-container")
        assertElementPresent("#neighbourhood-container")
        assertElementPresent("#neighbourhood-container .neighbourhood-card")
        assertElementPresent("#neighbourhood-container [data-component-id=map]")
        assertElementPresent("#school-container")
        assertElementPresent("#hospital-container")
        assertElementPresent("#market-container")
        assertElementPresent("#todo-container")
    }

    @Test
    fun sendMessage() {
        // WHEN
        navigateTo(listing.publicUrl!!)
        scroll(.33)

        reset(publisher)
        click("#btn-send-message")

        // THEN
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, atLeast(1)).publish(event.capture())
        assertEquals(PageName.WHATSAPP, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.MESSAGE, event.firstValue.track.event)
        assertEquals(listing.id.toString(), event.firstValue.track.productId)
        assertEquals("user:${listing.sellerAgentUserId}", event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertNotNull(event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
        assertEquals(ObjectType.LISTING, event.firstValue.track.productType)
    }

    @Test
    fun `open agent`() {
        doReturn(USER_ID).whenever(userIdProvider).get()

        navigateTo("${listing.publicUrl}?lang=fr")

        scroll(.33)
        click(".agent-info a")
        assertCurrentPageIs(PageName.AGENT)
    }

    @Test
    fun share() {
        navigateTo(listing.publicUrl!!)

        click("#btn-share-navbar")
        assertElementVisible("#koki-modal")

        assertElementVisible("#btn-share-facebook")
        assertElementVisible("#btn-share-twitter")
        assertElementVisible("#btn-share-email")
    }

    @Test
    fun sold() {
        setupListing(ListingStatus.SOLD)
        navigateTo(listing.publicUrl!!)
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent(".listing-status")
        assertElementNotPresent("#listing-price")
        assertElementNotPresent(".listing-contact-details .message")
        assertElementNotPresent(".listing-contact-details #btn-send-message")
    }

    @Test
    fun expired() {
        setupListing(ListingStatus.EXPIRED)
        navigateTo(listing.publicUrl!!)
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun draft() {
        setupListing(ListingStatus.DRAFT)
        navigateTo(listing.publicUrl!!)
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun cancelled() {
        setupListing(ListingStatus.CANCELLED)
        navigateTo(listing.publicUrl!!)
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `show - english translation`() {
        navigateTo("/listings/${listing.id}?lang=en")
        assertCurrentPageIs(PageName.LISTING)

        assertElementAttribute("html", "lang", "en")
    }

    @Test
    fun `show amenities`() {
        navigateTo(listing.publicUrl!!)

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

        assertElementVisible("#koki-modal")
        assertElementCount("#koki-modal .modal-body img", FileFixtures.images.size)
        click("#koki-modal .btn-close")
    }

    @Test
    fun `show images from hero`() {
        navigateTo("/listings/${listing.id}")

        click(".hero-image-container a")

        assertElementVisible("#koki-modal")
        assertElementCount("#koki-modal .modal-body img", FileFixtures.images.size)
        click("#koki-modal .btn-close")
    }

    @Test
    fun `VIEW tracking event on page load`() {
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
        assertEquals(ObjectType.LISTING, event.firstValue.track.productType)
    }

    @Test
    fun `EXIT tracking event on page unload`() {
        navigateTo("/listings/${listing.id}")
        Thread.sleep(1000)
        reset(publisher)

        navigateTo("/")
        Thread.sleep(1000)
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())

        assertEquals(PageName.LISTING, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.EXIT, event.firstValue.track.event)
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
        assertEquals(ObjectType.LISTING, event.firstValue.track.productType)
    }

    @Test
    fun `error when fetching places`() {
        // GIVEN
        doThrow(IllegalStateException::class).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchPlaceResponse::class.java)
            )

        // WHEN
        navigateTo("/listings/${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#neighbourhood-container")
        assertElementNotPresent("#neighbourhood-container .neighbourhood-card")
        assertElementNotPresent("#school-container")
        assertElementNotPresent("#hospital-container")
        assertElementNotPresent("#market-container")
        assertElementNotPresent("#todo-container")
    }

    @Test
    fun `listing with no geo-location`() {
        // GIVEN
        setupListing(geoLocation = null)

        // WHEN
        navigateTo("/listings/${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#neighbourhood-container")
        assertElementNotPresent("#neighbourhood-container [data-component-id=map]")
    }

    @Test
    fun `listing with no address`() {
        // GIVEN
        setupListing(address = null)

        // WHEN
        navigateTo("/listings/${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)
        assertElementNotPresent("#neighbourhood-container")
    }

    @Test
    fun `error when fetching similar listings`() {
        // GIVEN
        doThrow(IllegalStateException::class).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchSimilarListingResponse::class.java)
            )

        // WHEN
        navigateTo("/listings/${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)
        assertElementNotPresent("#similar-listing-container")
    }

    private fun setupListing(
        status: ListingStatus = ListingStatus.ACTIVE,
        geoLocation: GeoLocation? = listing.geoLocation,
        address: Address? = listing.address,
    ) {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(status = status, geoLocation = geoLocation, address = address)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )
    }
}
