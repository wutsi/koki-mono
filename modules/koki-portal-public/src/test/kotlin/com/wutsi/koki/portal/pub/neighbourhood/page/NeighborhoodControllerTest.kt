package com.wutsi.koki.portal.pub.neighbourhood.page

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
import com.wutsi.koki.listing.dto.SearchListingMetricResponse
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.place.dto.SearchPlaceResponse
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.AgentFixtures.agent
import com.wutsi.koki.portal.pub.PlaceFixtures.neighborhood
import com.wutsi.koki.portal.pub.RefDataFixtures.cities
import com.wutsi.koki.portal.pub.RefDataFixtures.neighborhoods
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.TenantFixtures.tenants
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NeighborhoodControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

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
        )
            .whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )
    }

    @Test
    fun show() {
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute(
            "head meta[name='description']",
            "content",
            neighborhood.summaryFr
        )

        // Opengraph
        assertElementAttribute(
            "head meta[property='og:title']",
            "content",
            "Guide du quartier ${neighborhoods[0].name},${cities[0].name} | Vivre à ${neighborhoods[0].name},${cities[0].name} | ${tenants[0].name}"
        )
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:description']",
            "content",
            neighborhood.summaryFr
        )
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/neighbourhoods/${neighborhoods[0].id}" + StringUtils.toSlug("", neighborhoods[0].name)
        )

        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementPresent("#introduction-container")
        assertElementPresent("#agent-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#sold-listing-container")
        assertElementPresent("#map-container")
        assertElementPresent("#about-container")
        assertElementPresent("#school-container")
        assertElementPresent("#hospital-container")
        assertElementPresent("#market-container")
        assertElementPresent("#todo-container")
        assertElementPresent("#similar-neighbourhood-container")
        assertElementPresent("#metric-container")
        assertElementPresent("#price-trend-container")
    }

    @Test
    fun `no content`() {
        doReturn(
            ResponseEntity(
                SearchPlaceResponse(emptyList()),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchPlaceResponse::class.java)
            )

        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementNotPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute(
            "head meta[property='og:title']",
            "content",
            "Guide du quartier ${neighborhoods[0].name},${cities[0].name} | Vivre à ${neighborhoods[0].name},${cities[0].name} | ${tenants[0].name}"
        )
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementNotPresent("head meta[property='og:description']")
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/neighbourhoods/${neighborhoods[0].id}" + StringUtils.toSlug("", neighborhoods[0].name)
        )

        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementNotPresent("#introduction-container")
        assertElementNotPresent("#about-container")
        assertElementNotPresent("#school-container")
        assertElementNotPresent("#hospital-container")
        assertElementNotPresent("#market-container")
        assertElementNotPresent("#todo-container")
        assertElementNotPresent("#similar-neighbourhood-container")
    }

    @Test
    fun `error when fetching metrics`() {
        // GIVEN
        doThrow(IllegalStateException::class).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingMetricResponse::class.java)
            )

        // WHEN
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // THEN
        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementNotPresent("#metric-container")
        assertElementNotPresent("#price-trend-container")
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
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // THEN
        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementNotPresent("#school-container")
        assertElementNotPresent("#hospital-container")
        assertElementNotPresent("#market-container")
        assertElementNotPresent("#todo-container")
    }

    @Test
    fun `error when fetching listings`() {
        // GIVEN
        doThrow(IllegalStateException::class).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        // WHEN
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // THEN
        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementNotPresent("#rental-listing-container")
        assertElementNotPresent("#sale-listing-container")
        assertElementNotPresent("#sold-listing-container")
        assertElementNotPresent("#map-container")
    }

    @Test
    fun sendMessage() {
        // WHEN
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")
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
        assertEquals(neighborhoods[0].id.toString(), event.firstValue.track.productId)
        assertEquals("user:${agent.userId}", event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertNotNull(event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
        assertEquals(ObjectType.PLACE, event.firstValue.track.productType)
    }
}
