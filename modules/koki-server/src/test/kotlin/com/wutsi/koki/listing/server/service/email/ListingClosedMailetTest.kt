package com.wutsi.koki.listing.server.service.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingClosedMailetTest : AbstractListingMailetTest() {
    private val agentService = mock(AgentService::class.java)
    private val mailet = ListingClosedMailet(
        listingService = listingService,
        userService = userService,
        locationService = locationService,
        templateResolver = templateResolver,
        tenantService = tenantService,
        fileService = fileService,
        messages = messages,
        sender = sender,
        agentService = agentService,
        logger = logger
    )

    private val buyer = UserEntity(
        id = listing.buyerAgentUserId,
        tenantId = 1L,
        displayName = "RAY SPONSIBLE",
        email = "ray.sponsible@gmail.com",
        employer = "REIMAX LAVAL",
        mobile = "+15147580000",
        photoUrl = "https://picsum.photos/200/200"
    )

    private val agent = AgentEntity(
        id = 7777L,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(buyer).whenever(userService).get(eq(buyer.id!!), any())
        doReturn(agent).whenever(agentService).getByUser(any(), any())
    }

    @Test
    fun sold() {
        doReturn(listing.copy(listingType = ListingType.SALE)).whenever(listingService).get(any(), any())

        val event = createEvent(status = ListingStatus.SOLD)
        val result = mailet.service(event)

        assertEquals(true, result)

        val bodyArg = argumentCaptor<String>()
        val subjectArg = argumentCaptor<String>()
        verify(sender).send(
            eq(buyer),
            subjectArg.capture(),
            bodyArg.capture(),
            eq(emptyList()),
            eq(event.tenantId)
        )

        assertEquals(
            "La propriété est VENDUE!",
            subjectArg.firstValue
        )
        assertEquals(
            """
<center class="text-larger">
    Nous avons le plaisir de vous informer que la transaction du listing à l'adresse <em>340 Pascal, Bastos, Yaounde</em> a été
    clôturée.
</center>

<p>
    Cette transaction a été réalisée au prix de <b>C$ 175,000</b>, en co-courtage avec l'agent <a
    href="https://realtor.com/agents/7777"><b>John Smith (REIMAX LAVAL)</b></a>.
    <br/>
    Votre commission s'élève à <b>3.0%</b>.
</p>

<br/><br/>

<center>
    <table cellpadding="0" cellspacing="0" style="border: 1px solid gray; border-radius: 0.5em" width="400">
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
    fun rent() {
        doReturn(listing.copy(listingType = ListingType.RENTAL)).whenever(listingService).get(any(), any())

        val event = createEvent(status = ListingStatus.RENTED)
        val result = mailet.service(event)

        assertEquals(true, result)

        val bodyArg = argumentCaptor<String>()
        val subjectArg = argumentCaptor<String>()
        verify(sender).send(
            eq(buyer),
            subjectArg.capture(),
            bodyArg.capture(),
            eq(emptyList()),
            eq(event.tenantId)
        )

        assertEquals(
            "La propriété est LOUÉE!",
            subjectArg.firstValue
        )
        assertEquals(
            """
<center class="text-larger">
    Nous avons le plaisir de vous informer que la transaction du listing à l'adresse <em>340 Pascal, Bastos, Yaounde</em> a été
    clôturée.
</center>

<p>
    Cette transaction a été réalisée au prix de <b>C$ 175,000/mo</b>, en co-courtage avec l'agent <a
    href="https://realtor.com/agents/7777"><b>John Smith (REIMAX LAVAL)</b></a>.
    <br/>
    Votre commission s'élève à <b>3.0%</b>.
</p>

<br/><br/>

<center>
    <table cellpadding="0" cellspacing="0" style="border: 1px solid gray; border-radius: 0.5em" width="400">
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
                    <b class="text-larger">C$ 150,000/mo</b><br/><br/>
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
    fun `event status not supported`() {
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
