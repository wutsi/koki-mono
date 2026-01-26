package com.wutsi.koki.portal.pub.agent.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.listing.dto.SearchListingMetricResponse
import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.AgentFixtures.agent
import com.wutsi.koki.portal.pub.FileFixtures
import com.wutsi.koki.portal.pub.RefDataFixtures.cities
import com.wutsi.koki.portal.pub.RefDataFixtures.locations
import com.wutsi.koki.portal.pub.RefDataFixtures.neighborhoods
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.TenantFixtures.tenants
import com.wutsi.koki.portal.pub.UserFixtures.user
import com.wutsi.koki.portal.pub.common.page.PageName
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

class AgentControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var ipService: GeoIpService

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
    }

    @Test
    fun show() {
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.AGENT)

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute(
            "head meta[name='description']",
            "content",
            "Agent immobilier Ray Sponsible de Quebec,Canada. Consultez les listings de l'agent et contactez-le pour tous vos besoins immobiliers."
        )

        // Opengraph
        assertElementAttribute(
            "head meta[property='og:title']",
            "content",
            "${user.displayName} | Agent Immobilier à ${locations[0].name} | ${tenants[0].name}"
        )
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:description']",
            "content",
            "Agent immobilier Ray Sponsible de Quebec,Canada. Consultez les listings de l'agent et contactez-le pour tous vos besoins immobiliers."
        )
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/agents/${agent.id}" + StringUtils.toSlug("", user.displayName)
        )

        assertCurrentPageIs(PageName.AGENT)
        assertElementPresent("#metric-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sold-listing-container")
        assertElementPresent("#map-container")
        assertElementPresent("#price-trend-container")
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
        assertElementNotPresent("#metrics-container")
        assertElementNotPresent("#price-trend-container")
    }

    @Test
    fun sendMessage() {
        navigateTo("/agents/${agent.id}")
        scroll(.33)
        click("#btn-send-message")

        // THEN
        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, atLeast(1)).publish(event.capture())
        assertEquals(PageName.AGENT, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.MESSAGE, event.firstValue.track.event)
        assertEquals(agent.id.toString(), event.firstValue.track.productId)
        assertEquals(null, event.firstValue.track.value)
        assertEquals(null, event.firstValue.track.accountId)
        assertEquals(ChannelType.WEB, event.firstValue.track.channelType)
        assertEquals(USER_AGENT, event.firstValue.track.ua)
        assertEquals("0:0:0:0:0:0:0:1", event.firstValue.track.ip)
        assertEquals(null, event.firstValue.track.lat)
        assertEquals(null, event.firstValue.track.long)
        assertNotNull(event.firstValue.track.url)
        assertEquals(null, event.firstValue.track.rank)
        assertEquals(ObjectType.AGENT, event.firstValue.track.productType)
        assertEquals(agent.userId.toString(), event.firstValue.track.recipientId)
    }
}
