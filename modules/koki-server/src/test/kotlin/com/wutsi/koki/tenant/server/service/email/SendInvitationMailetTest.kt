package com.wutsi.koki.tenant.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import com.wutsi.koki.tenant.dto.event.InvitationCreatedEvent
import com.wutsi.koki.tenant.server.domain.InvitationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.InvitationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class SendInvitationMailetTest {
    private val invitationService = mock<InvitationService>()
    private val tenantService = mock<TenantService>()
    private val templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    private val templateResolver = EmailTemplateResolver(templateEngine)

    private val sender = mock<Sender>()
    private val mailet = SendInvitationMailet(
        invitationService,
        tenantService,
        templateResolver,
        templateEngine,
        sender,
    )

    val tenant = TenantEntity(
        id = 1L,
        name = "BlueKoki",
        portalUrl = "https://koki-portal.herokuapp.com",
        country = "CM",
    )

    val invitation = InvitationEntity(
        id = "1111",
        tenantId = tenant.id!!,
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        type = InvitationType.AGENT,
        status = InvitationStatus.PENDING,
    )

    @BeforeEach
    fun setUp() {
        doReturn(invitation).whenever(invitationService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
    }

    @Test
    fun sendToAgent() {
        val event = InvitationCreatedEvent(invitationId = invitation.id!!, tenantId = invitation.tenantId)
        val result = mailet.service(event)

        assertEquals(true, result)

        val recipient = argumentCaptor<Party>()
        val subject = argumentCaptor<String>()
        val body = argumentCaptor<String>()
        verify(sender).send(
            recipient.capture(),
            subject.capture(),
            body.capture(),
            eq(emptyList<File>()),
            eq(event.tenantId),
        )

        assertEquals(invitation.displayName, recipient.firstValue.displayName)
        assertEquals(invitation.email, recipient.firstValue.email)
        assertEquals("Invitation Exclusive | Rejoignez BlueKoki, le futur de l'Immobilier!", subject.firstValue)
        assertEquals(
            """
                Ray Sponsible,<br/><br/>

                Nous avons le plaisir de vous inviter à vous joindre à la plateforme BlueKoki,
                la première plateforme moderne conçue pour dynamiser le marché immobilier au Cameroun.<br/><br/>

                Notre objectif est simple :
                <b>
                    centraliser l'offre immobilières et faciliter la collaboration entre
                    agents sérieux pour conclure des ventes plus rapidement et efficacement.
                </b>
                <br/><br/>

                En tant qu'agent reconnu, votre expertise est précieuse.
                Nous vous invitons à nous rejoindre en tant que membre avec des avantages exclusifs :
                <ul>
                    <li>Accès gratuit sur la plateforme.</li>
                    <li>Visibilité accrue pour vos annonces.</li>
                    <li>Un réseau d'agents fiables pour développer vos affaires.</li>
                    <li>Rejoignez-nous en cliquant ici : https://koki-portal.herokuapp.com/signup?inv&#61;1111</li>
                </ul>

                <center>
                    <a class="btn btn-primary" href="https://koki-portal.herokuapp.com/signup?inv&#61;1111">Inscrivez-vous</a><br/><br/>
                </center>

                Prêts à révolutionner l'immobilier ensemble ? <br/><br/>

                Bien cordialement,<br/><br/>

                BlueKoki

            """.trimIndent(),
            body.firstValue,
        )
    }

    @Test
    fun sendToUnknown() {
        doReturn(invitation.copy(type = InvitationType.UNKNOWN)).whenever(invitationService).get(any(), any())

        val event = InvitationCreatedEvent(invitationId = invitation.id!!, tenantId = invitation.tenantId)
        val result = mailet.service(event)

        assertEquals(true, result)

        verify(sender, never()).send(
            any<Party>(),
            any<String>(),
            any<String>(),
            any(),
            any(),
        )
    }

    @Test
    fun `invitation not pending`() {
        doReturn(invitation.copy(status = InvitationStatus.EXPIRED)).whenever(invitationService).get(any(), any())

        val event = InvitationCreatedEvent(invitationId = invitation.id!!, tenantId = invitation.tenantId)
        val result = mailet.service(event)

        assertEquals(true, result)

        verify(sender, never()).send(
            any<Party>(),
            any<String>(),
            any<String>(),
            any(),
            any(),
        )
    }

    @Test
    fun `invitation deleted`() {
        val ex = NotFoundException(error = Error())
        doThrow(ex).whenever(invitationService).get(any(), any())

        val event = InvitationCreatedEvent(invitationId = invitation.id!!, tenantId = invitation.tenantId)
        val result = mailet.service(event)

        assertEquals(true, result)

        verify(sender, never()).send(
            any<Party>(),
            any<String>(),
            any<String>(),
            any(),
            any(),
        )
    }

    @Test
    fun `unsupported command`() {
        val command = mapOf("foo" to "bar")
        val result = mailet.service(command)

        assertEquals(false, result)
    }
}
