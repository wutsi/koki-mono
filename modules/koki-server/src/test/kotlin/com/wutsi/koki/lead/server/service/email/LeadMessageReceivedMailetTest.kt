package com.wutsi.koki.lead.server.service.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.listing.server.service.email.AbstractListingMailetTest
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class LeadMessageReceivedMailetTest : AbstractListingMailetTest() {
    private val leadMessageService = Mockito.mock<LeadMessageService>()
    private val mailet = LeadMessageReceivedMailet(
        userService = userService,
        locationService = locationService,
        templateResolver = templateResolver,
        tenantService = tenantService,
        leadMessageService = leadMessageService,
        fileService = fileService,
        sender = sender,
        messages = messages,
    )

    private val user = UserEntity(
        id = 4309L,
        displayName = "Roger Milla",
        email = "roger.milla@gmail.com",
        mobile = "+237650000000",
    )
    private val lead = LeadEntity(
        id = 222L,
        listing = listing,
        agentUserId = sellerAgent.id!!,
        userId = user.id!!,
    )

    private val message = LeadMessageEntity(
        id = 555L,
        lead = lead,
        content = "Hello, I am interested in your listing.",
        createdAt = DateUtils.addHours(Date(), -3)
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(1L).whenever(leadMessageService).getMessageRank(any())
        doReturn(message).whenever(leadMessageService).get(any(), any())
        doReturn(listOf(user, sellerAgent)).whenever(userService)
            .search(
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    @Test
    fun `new lead from listing`() {
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
        assertEquals(
            "NOUVEAU PROSPECT: Demande d'information pour 340 Pascal, Bastos, Yaounde (Roger Milla)",
            subjectArg.firstValue
        )
        assertEquals(
            """
                <h3>Nouvelle Demande de Prospect (Action Requise!)</h3>

                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td><b>Nom du Prospect:</b></td>
                        <td>Roger Milla</td>
                    </tr>
                    <tr>
                        <td><b>Numero de téléphone:</b></td>
                        <td>+237 6 50 00 00 00</td>
                    </tr>
                    <tr>
                        <td><b>Email:</b></td>
                        <td>roger.milla@gmail.com</td>
                    </tr>
                </table>

                <h3>Message du Prospect</h3>
                <div class="padding box-highlight-light rounded">
                    Hello, I am interested in your listing.
                </div>

                <h3>Prochaines Étapes</h3>
                <ol>
                    <li>
                        <b>Réponse Prioritaire :</b> Contactez le prospect dans les 15 minutes.
                    </li>
                    <li>
                        <b>Planification :</b> Confirmez l'heure de la visite ou proposez des alternatives.
                    </li>
                    <li>
                        <b>Mise à Jour :</b> Enregistrez cette interaction dans le dossier du prospect.
                    </li>
                </ol>

                <h3>Liens d'action rapide</h3>
                <ol>
                    <li>
                        Voir le Profil de Roger Milla en ligne. <a href="https://realtor.com/leads/222">Voir le profil</a>
                    </li>
                    <li>
                        Appeler Roger Milla maintenant: <a href="tel:237650000000">+237 6 50 00 00 00</a>.
                    </li>
                </ol>

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
    fun `5th lead message from listing`() {
        doReturn(5L).whenever(leadMessageService).getMessageRank(any())

        val event = createEvent(new = false)
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
        assertEquals(
            "\uD83D\uDD25 PROSPECT ENGAGÉ: 5ème Message pour 340 Pascal, Bastos, Yaounde (Roger Milla)",
            subjectArg.firstValue
        )
        assertEquals(
            """
               <h3>Message de Suivi (Engagement Élevé)</h3>

               <table cellpadding="0" cellspacing="0">
                   <tr>
                       <td><b>Nom du Prospect:</b></td>
                       <td>Roger Milla</td>
                   </tr>
                   <tr>
                       <td><b>Numero de téléphone:</b></td>
                       <td>+237 6 50 00 00 00</td>
                   </tr>
                   <tr>
                       <td><b>Email:</b></td>
                       <td>roger.milla@gmail.com</td>
                   </tr>
               </table>

               <h3>Message du Prospect</h3>
               <div class="padding box-highlight-light rounded">
                   Hello, I am interested in your listing.
               </div>

               <h3>Prochaines Étapes</h3>
               <ol>
                   <li>
                       <b>Réponse Prioritaire :</b> Contactez le prospect dans les 15 minutes.
                   </li>
                   <li>
                       <b>Planification :</b> Confirmez l'heure de la visite ou proposez des alternatives.
                   </li>
                   <li>
                       <b>Mise à Jour :</b> Enregistrez cette interaction dans le dossier du prospect.
                   </li>
               </ol>

               <h3>Liens d'action rapide</h3>
               <ol>
                   <li>
                       Voir le Profil de Roger Milla en ligne. <a href="https://realtor.com/leads/222">Voir le profil</a>
                   </li>
                   <li>
                       Appeler Roger Milla maintenant: <a href="tel:237650000000">+237 6 50 00 00 00</a>.
                   </li>
               </ol>

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
    fun `lead from agent`() {
        doReturn(
            message.copy(lead = lead.copy(listing = null))
        ).whenever(leadMessageService).get(any(), any())

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
        assertEquals("NOUVEAU PROSPECT : Demande de contact (Roger Milla)", subjectArg.firstValue)
        assertEquals(
            """
                <h3>Nouvelle Demande de Contact Direct</h3>
                <div>
                    Ceci est un prospect qui a choisi de vous contacter directement en se basant sur votre profil
                </div>

                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td><b>Nom du Prospect:</b></td>
                        <td>Roger Milla</td>
                    </tr>
                    <tr>
                        <td><b>Numero de téléphone:</b></td>
                        <td>+237 6 50 00 00 00</td>
                    </tr>
                    <tr>
                        <td><b>Email:</b></td>
                        <td>roger.milla@gmail.com</td>
                    </tr>
                </table>

                <h3>Message du Prospect</h3>
                <div class="padding box-highlight-light rounded">
                    Hello, I am interested in your listing.
                </div>


                <h3>Prochaines Étapes</h3>
                <ol>
                    <li>
                        <b>Réponse Prioritaire :</b> Répondez dans l'heure. Remerciez le prospect pour avoir choisi votre profil et
                        confirmez la réception de sa demande.
                    </li>
                    <li>
                        <b>Mise à Jour :</b> Enregistrez cette interaction dans le dossier du prospect.
                    </li>
                </ol>

                <h3>Liens d'action rapide</h3>
                <ol>
                    <li>
                        Voir le Profil de Roger Milla en ligne. <a href="https://realtor.com/leads/222">Voir le profil</a>
                    </li>
                    <li>
                        Appeler Roger Milla maintenant: <a href="tel:237650000000">+237 6 50 00 00 00</a>.
                    </li>
                </ol>

            """.trimIndent(),
            bodyArg.firstValue,
        )
    }

    @Test
    fun `4th lead message from agent`() {
        doReturn(4L).whenever(leadMessageService).getMessageRank(any())

        doReturn(
            message.copy(lead = lead.copy(listing = null))
        ).whenever(leadMessageService).get(any(), any())

        val event = createEvent(new = false)
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
        assertEquals("\uD83D\uDD25 PROSPECT ENGAGÉ : 4ème Message (Roger Milla)", subjectArg.firstValue)
        assertEquals(
            """
                <h3>Nouvelle Demande de Contact Direct</h3>
                <div>
                    Ceci est un prospect qui a choisi de vous contacter directement en se basant sur votre profil
                </div>

                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td><b>Nom du Prospect:</b></td>
                        <td>Roger Milla</td>
                    </tr>
                    <tr>
                        <td><b>Numero de téléphone:</b></td>
                        <td>+237 6 50 00 00 00</td>
                    </tr>
                    <tr>
                        <td><b>Email:</b></td>
                        <td>roger.milla@gmail.com</td>
                    </tr>
                </table>

                <h3>Message du Prospect</h3>
                <div class="padding box-highlight-light rounded">
                    Hello, I am interested in your listing.
                </div>


                <h3>Prochaines Étapes</h3>
                <ol>
                    <li>
                        <b>Réponse Prioritaire :</b> Répondez dans l'heure. Remerciez le prospect pour avoir choisi votre profil et
                        confirmez la réception de sa demande.
                    </li>
                    <li>
                        <b>Mise à Jour :</b> Enregistrez cette interaction dans le dossier du prospect.
                    </li>
                </ol>

                <h3>Liens d'action rapide</h3>
                <ol>
                    <li>
                        Voir le Profil de Roger Milla en ligne. <a href="https://realtor.com/leads/222">Voir le profil</a>
                    </li>
                    <li>
                        Appeler Roger Milla maintenant: <a href="tel:237650000000">+237 6 50 00 00 00</a>.
                    </li>
                </ol>

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

    private fun createEvent(new: Boolean = true): LeadMessageReceivedEvent {
        return LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = listing.tenantId,
            newLead = new,
        )
    }
}
