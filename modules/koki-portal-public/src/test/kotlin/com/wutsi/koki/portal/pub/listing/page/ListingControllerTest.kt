package com.wutsi.koki.portal.pub.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.FileFixtures
import com.wutsi.koki.portal.pub.ListingFixtures.listing
import com.wutsi.koki.portal.pub.ListingFixtures.similar
import com.wutsi.koki.portal.pub.RefDataFixtures.cities
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.UserFixtures.user
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.user.service.UserIdProvider
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
        assertElementAttribute("#listing-map", "data-longitude", listing.geoLocation?.longitude?.toString())
        assertElementAttribute("#listing-map", "data-latitude", listing.geoLocation?.latitude?.toString())
        assertElementAttribute("#listing-map", "data-show-marker", "true")
        assertElementAttribute("#listing-map", "data-zoom", "18")

        // Content
        assertElementPresent("#btn-share-navbar")
        assertElementNotPresent(".listing-status") // Status badge - only for sold listings
        assertElementCount("#similar-listings .listing-card", similar.size) // Similar listings
    }

    @Test
    fun sendMessage() {
        navigateTo(listing.publicUrl!!)

        scroll(.33)
        click("#btn-send-message")
        assertElementVisible("#koki-modal")

        input("#firstName", "Ray")
        input("#lastName", "Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#message", "I am interested in your property. Please contact me.")
        input("#phone", "5147580100")
        click("#btn-send")

        val request = argumentCaptor<CreateLeadRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads"),
            request.capture(),
            eq(CreateLeadResponse::class.java)
        )
        assertEquals(listing.id, request.firstValue.listingId)
        assertEquals(null, request.firstValue.agentUserId)
        assertEquals(LeadSource.LISTING, request.firstValue.source)
        assertEquals("Ray", request.firstValue.firstName)
        assertEquals("Sponsible", request.firstValue.lastName)
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)
        assertEquals("I am interested in your property. Please contact me.", request.firstValue.message)
        assertEquals("+15147580100", request.firstValue.phoneNumber)
        assertEquals("CA", request.firstValue.country)
        assertNotNull(request.firstValue.cityId)

        assertCurrentPageIs(PageName.LISTING)
        assertElementVisible("#toast-message")
    }

    @Test
    fun `sendMessage no IP - country resolved from IP`() {
        doReturn(null).whenever(ipService).resolve(any())

        navigateTo(listing.publicUrl!!)

        scroll(.33)
        click("#btn-send-message")
        assertElementVisible("#koki-modal")

        input("#firstName", "Ray")
        input("#lastName", "Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#message", "I am interested in your property. Please contact me.")
        input("#phone", "6467580100")
        click("#btn-send")

        val request = argumentCaptor<CreateLeadRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads"),
            request.capture(),
            eq(CreateLeadResponse::class.java)
        )
        assertEquals("Ray", request.firstValue.firstName)
        assertEquals("Sponsible", request.firstValue.lastName)
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)
        assertEquals("I am interested in your property. Please contact me.", request.firstValue.message)
        assertEquals("+16467580100", request.firstValue.phoneNumber)
        assertEquals("US", request.firstValue.country)
        assertEquals(null, request.firstValue.cityId)

        assertCurrentPageIs(PageName.LISTING)
        assertElementVisible("#toast-message")
    }

    @Test
    fun `sendMessage for logged in user`() {
        doReturn(USER_ID).whenever(userIdProvider).get()

        navigateTo("${listing.publicUrl}?lang=fr")

        scroll(.33)
        click("#btn-send-message")
        assertElementVisible("#koki-modal")

        assertElementAttribute("#email", "disabled", "true")
        click("#btn-send")

        val request = argumentCaptor<CreateLeadRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads"),
            request.capture(),
            eq(CreateLeadResponse::class.java)
        )
        assertEquals("Ray", request.firstValue.firstName)
        assertEquals("Sponsible", request.firstValue.lastName)
        assertEquals(user.email, request.firstValue.email)
        assertEquals("Bonjour, je suis intéressé par cette propriété.", request.firstValue.message)
        assertEquals(user.mobile, request.firstValue.phoneNumber)
        assertEquals("CA", request.firstValue.country)
        assertNotNull(request.firstValue.cityId)

        assertCurrentPageIs(PageName.LISTING)
        assertElementVisible("#toast-message")
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

    private fun setupListing(status: ListingStatus) {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(status = status)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )
    }
}
