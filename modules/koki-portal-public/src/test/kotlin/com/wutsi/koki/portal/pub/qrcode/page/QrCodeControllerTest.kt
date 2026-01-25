package com.wutsi.koki.portal.pub.qrcode.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.AgentFixtures
import com.wutsi.koki.portal.pub.ListingFixtures
import com.wutsi.koki.portal.pub.TenantFixtures
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QrCodeControllerTest : AbstractPageControllerTest() {
    @Test
    fun agent() {
        // WHEN
        navigateTo("/qr-codes/agents/${AgentFixtures.agent.id}")

        // THEN
        assertCurrentPageIs(PageName.AGENT)

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, atLeast(1)).publish(event.capture())
        assertEquals(PageName.QR, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.QR_CODE_SCAN, event.firstValue.track.event)
        assertEquals(AgentFixtures.agent.id.toString(), event.firstValue.track.productId)
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
    }

    @Test
    fun listing() {
        // WHEN
        navigateTo("/qr-codes/listings/${ListingFixtures.listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, atLeast(1)).publish(event.capture())
        assertEquals(PageName.QR, event.firstValue.track.page)
        assertNotNull(event.firstValue.track.correlationId)
        assertNotNull(event.firstValue.track.deviceId)
        assertEquals(TenantFixtures.tenants[0].id, event.firstValue.track.tenantId)
        assertEquals(null, event.firstValue.track.component)
        assertEquals(TrackEvent.QR_CODE_SCAN, event.firstValue.track.event)
        assertEquals(ListingFixtures.listing.id.toString(), event.firstValue.track.productId)
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
    fun `listing - no public url`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                GetListingResponse(ListingFixtures.listing.copy(publicUrl = null)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        // WHEN
        navigateTo("/qr-codes/listings/${ListingFixtures.listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING)

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher, atLeast(1)).publish(event.capture())
        assertEquals(PageName.QR, event.firstValue.track.page)
        assertEquals(TrackEvent.QR_CODE_SCAN, event.firstValue.track.event)
        assertEquals(ListingFixtures.listing.id.toString(), event.firstValue.track.productId)
        assertEquals(ObjectType.LISTING, event.firstValue.track.productType)
    }
}
