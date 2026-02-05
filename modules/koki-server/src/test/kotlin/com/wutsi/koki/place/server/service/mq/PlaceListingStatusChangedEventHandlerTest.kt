package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.refdata.dto.LocationType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaceListingStatusChangedEventHandlerTest {
    private val worker = mock<PlaceContentGeneratorWorker>()
    private val listingService = mock<ListingService>()
    private val logger = DefaultKVLogger()

    private val handler = PlaceListingStatusChangedEventHandler(
        listingService = listingService,
        worker = worker,
        logger = logger,
    )

    private val listing = ListingEntity(
        id = 100L,
        tenantId = 1L,
        neighbourhoodId = 111L,
        cityId = 222L
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `listing status=ACTIVE - generate content of city and neighbourhood`() {
        assertTrue {
            handler.handle(
                ListingStatusChangedEvent(
                    listingId = listing.id!!,
                    tenantId = listing.tenantId,
                    status = ListingStatus.ACTIVE,
                )
            )
        }

        verify(worker).generate(listing.neighbourhoodId!!, LocationType.NEIGHBORHOOD)
        verify(worker).generate(listing.cityId!!, LocationType.CITY)
    }

    @Test
    fun `listing status not ACTIVE - dont generate content`() {
        ListingStatus.entries
            .filter { it != ListingStatus.ACTIVE }
            .forEach { status ->
                `no content generate`(status)
            }
    }

    private fun `no content generate`(status: ListingStatus) {
        assertFalse {
            handler.handle(
                ListingStatusChangedEvent(
                    listingId = listing.id!!,
                    tenantId = listing.tenantId,
                    status = status,
                )
            )
        }

        verify(worker, never()).generate(any(), any())
    }
}
