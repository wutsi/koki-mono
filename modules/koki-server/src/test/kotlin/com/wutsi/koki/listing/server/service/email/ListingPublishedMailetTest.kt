package com.wutsi.koki.listing.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingPublishedMailetTest {
    private val listingService = mock<ListingService>()
    private val userService = mock<UserService>()
    private val locationService = mock<LocationService>()
    private val tenantService = mock<TenantService>()
    private val templateResolver = EmailTemplateResolver(MustacheTemplatingEngine(DefaultMustacheFactory()))
    private val sender = mock<Sender>()
    private val logger: KVLogger = DefaultKVLogger()
    private val mailet = ListingPublishedMailet(
        listingService = listingService,
        userService = userService,
        locationService = locationService,
        templateResolver = templateResolver,
        tenantService = tenantService,
        sender = sender,
        logger = logger
    )

    private val city = LocationEntity(id = 111, name = "Yaounde", country = "CM")
    private val neighbourhood = LocationEntity(id = 222, name = "Bastos", country = "CM")
    private val tenant = TenantEntity(
        id = 1L,
        name = "Test",
        clientPortalUrl = "https://realtor.com",
        logoUrl = "https://picsum.photos/200/200",
    )
    private val listing = ListingEntity(
        id = 111L,
        tenantId = tenant.id!!,
        status = ListingStatus.ACTIVE,
        listingType = ListingType.RENTAL,
        sellerContactId = 111L,
        sellerAgentUserId = 333L,
        street = "340 Pascal",
        cityId = city.id,
        neighbourhoodId = neighbourhood.id,
    )

    private val agent = UserEntity(
        id = 333L,
        tenantId = 1L,
        displayName = "JOHN SMITH",
        email = "john.smith@gmail.com",
        employer = "REIMAX LAVAL",
        mobile = "+15147580011",
        photoUrl = "https://picsum.photos/200/200"
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(agent).whenever(userService).get(any(), any())
        doReturn(city).whenever(locationService).get(city.id!!)
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun published() {
        doReturn(listing.copy(listingType = ListingType.SALE)).whenever(listingService).get(any(), any())

        val event = createEvent()
        val result = mailet.service(event)

        assertEquals(true, result)

        val bodyArg = argumentCaptor<String>()
        verify(sender).send(
            eq(agent),
            eq(ListingPublishedMailet.SUBJECT),
            bodyArg.capture(),
            eq(emptyList()),
            eq(event.tenantId)
        )

        assertEquals(
            """
                Bonjour JOHN SMITH,<br/><br/>

                Excellente nouvelle! Votre listing <a href="/listings/111">#-1</a> situé au
                &nbsp;<b>340 Pascal, Bastos, Yaounde</b> a été publiée avec succès!<br/>
                Il est maintenant visible par des tous les agents qui utilisent notre plateforme.<br/><br>

                Nous vous aviserons de toute demande de visite ou autre requêtes concernant votre listing.<br/><br>

                Cordialement,

            """.trimIndent(),
            bodyArg.firstValue,
        )
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
