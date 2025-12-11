package com.wutsi.koki.lead.server.service.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.server.service.email.AbstractListingMailetTest
import com.wutsi.koki.platform.messaging.Party
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertEquals

class LeadCreatedMailetTest : AbstractListingMailetTest() {
    private val leadService = Mockito.mock<LeadService>()
    private val mailet = LeadCreatedMailet(
        userService = userService,
        locationService = locationService,
        templateResolver = templateResolver,
        tenantService = tenantService,
        leadService = leadService,
        fileService = fileService,
        sender = sender,
        messages = messages,
    )

    private val lead = LeadEntity(
        id = 222L,
//        firstName = "Jimmy",
//        lastName = "Smith",
        listing = listing,
        agentUserId = sellerAgent.id!!,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(lead).whenever(leadService).get(any(), any())
    }

    @Test
    fun `lead from listing`() {
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
        assertEquals("Listing #111: Nouveau client potentiel, à vous de jouer!", subjectArg.firstValue)
        assertEquals(
            """
                <center class="text-larger">
                    Vous avez reçu un message d'un potential client pour votre listing <em>#111</em> à l'addresse <em>340 Pascal, Bastos, Yaounde</em>.
                    <br/><br/>
                    <a class="btn btn-primary" href="/leads/222">Lire le Message</a>
                </center>

                <br/><br/>

                <center>
                    <table width="400" cellspacing="0" cellpadding="0" style="border: 1px solid gray; border-radius: 0.5em">
                        <tr>
                            <td width="1%">
                                <a href="/listings/111" style="text-decoration: none; color: inherit;">
                                    <img src="https://picsum.photos/1200/800"
                                         style="width: 400px; height: 300px; object-fit: cover; border-radius: 0.5em 0.5em 0 0"
                                    />
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <td class="padding" valign="top">
                                <a href="/listings/111" style="text-decoration: none; color: inherit;">
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
    fun `lead from agent`() {
        doReturn(lead.copy(listing = null)).whenever(leadService).get(any(), any())

        val event = emptyMap<String, String>()
        val result = mailet.service(event)

        assertEquals(false, result)
        verify(sender, never()).send(any<Party>(), any(), any(), any(), any())
    }

    private fun createEvent(): LeadCreatedEvent {
        return LeadCreatedEvent(
            leadId = 222L,
            tenantId = listing.tenantId,
        )
    }
}
