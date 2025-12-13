package com.wutsi.koki.listing.server.service.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.messaging.Party
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingPublishedMailetTest : AbstractListingMailetTest() {
    private val mailet = ListingPublishedMailet(
        listingService = listingService,
        userService = userService,
        locationService = locationService,
        templateResolver = templateResolver,
        tenantService = tenantService,
        fileService = fileService,
        messages = messages,
        sender = sender,
        logger = logger
    )

    @Test
    fun published() {
        doReturn(listing.copy(listingType = ListingType.SALE)).whenever(listingService).get(any(), any())

        val event = createEvent()
        val result = mailet.service(event)

        assertEquals(true, result)

        val bodyArg = argumentCaptor<String>()
        val subjectArg = argumentCaptor<String>()

        verify(sender).send(
            eq(sellerAgent),
            subjectArg.capture(),
            bodyArg.capture(),
            eq(emptyList()),
            eq(event.tenantId)
        )
        assertEquals("Listing #111: Votre listing a été publiée", subjectArg.firstValue)
        assertEquals(
            """
                <center class="text-larger">
                    Votre listing listing <em>#111</em> à l'addresse <em>340 Pascal, Bastos, Yaounde</em>
                    a été publiée avec succès, et est maintenant disponible en ligne et visible par tous
                    les agents et visiteurs qui utilisent notre plateforme
                </center>

                <br/><br>

                <center>
                    <table width="400" cellspacing="0" cellpadding="0" style="border: 1px solid gray; border-radius: 0.5em">
                        <tr>
                            <td width="1%">
                                <a href="https://realtor.com/listings/111" style="text-decoration: none; color: inherit;">
                                    <img src="https://picsum.photos/1200/800"
                                         style="width: 400px; height: 300px; object-fit: cover; border-radius: 0.5em 0.5em 0 0"
                                    />
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <td class="padding" valign="top">
                                <a href="https://realtor.com/listings/111" style="text-decoration: none; color: inherit;">
                                    <b class="text-larger">C$ 150,000</b><br/><br/>
                                    3 Chambres | 2 Bains | 750 m2<br/>
                                    340 Pascal, Bastos, Yaounde<br/>
                                </a>
                            </td>
                        </tr>
                    </table>
                </center>

            """.trimIndent(),
            bodyArg.firstValue,
        )
    }

    @Test
    fun `event not supported`() {
        val event = emptyMap<String, String>()
        val result = mailet.service(event)

        assertEquals(false, result)
        verify(sender, never()).send(any<Party>(), any(), any(), any(), any())
    }

    @Test
    fun `event status not PUBLISHED`() {
        val event = createEvent(ListingStatus.PUBLISHING)
        val result = mailet.service(event)

        assertEquals(false, result)
        verify(sender, never()).send(any<Party>(), any(), any(), any(), any())
    }

    @Test
    fun `listing not active`() {
        doReturn(listing.copy(status = ListingStatus.PUBLISHING)).whenever(listingService).get(any(), any())

        val event = createEvent()
        val result = mailet.service(event)

        assertEquals(false, result)
        verify(sender, never()).send(any<Party>(), any(), any(), any(), any())
    }

    @Test
    fun `no agent`() {
        doReturn(listing.copy(sellerAgentUserId = null)).whenever(listingService).get(any(), any())

        val event = createEvent()
        val result = mailet.service(event)

        assertEquals(false, result)
        verify(sender, never()).send(any<Party>(), any(), any(), any(), any())
    }

    private fun createEvent(status: ListingStatus = ListingStatus.ACTIVE): ListingStatusChangedEvent {
        return ListingStatusChangedEvent(
            status = status,
            listingId = listing.id!!,
            tenantId = listing.tenantId,
        )
    }
}
