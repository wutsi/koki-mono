package com.wutsi.koki.listing.server.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.util.Date
import kotlin.test.Test

class ListingJobsTest {
    private val listingService = mock<ListingService>()
    private val listingMetricService = mock<ListingMetricService>()
    private val tenantService = mock<TenantService>()
    private val publisher = mock<Publisher>()
    private val jobs = ListingJobs(listingService, listingMetricService, tenantService, publisher)

    private val now = Date()
    private val earlier = DateUtils.addHours(now, -7)

    private val tenants = listOf(
        TenantEntity(id = 1, name = "Tenant 1", status = TenantStatus.ACTIVE),
        TenantEntity(id = 2, name = "Tenant 2", status = TenantStatus.ACTIVE),
        TenantEntity(id = 3, name = "Tenant 3", status = TenantStatus.NEW),
        TenantEntity(id = 3, name = "Tenant 3", status = TenantStatus.SUSPENDED),
    )

    val listing1s = listOf(
        ListingEntity(id = 111, tenantId = 1L, modifiedAt = earlier),
        ListingEntity(id = 112, tenantId = 1L, modifiedAt = earlier),
        ListingEntity(id = 113, tenantId = 1L, modifiedAt = now),
    )
    val listing2s = listOf(
        ListingEntity(id = 211, tenantId = 2L, modifiedAt = earlier),
    )
    val listing3s = listOf(
        ListingEntity(id = 311, tenantId = 3L, modifiedAt = earlier),
    )
    val listing4s = listOf(
        ListingEntity(id = 411, tenantId = 3L, modifiedAt = earlier),
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenants).whenever(tenantService).all()

        doReturn(listing1s)
            .doReturn(listing2s)
            .doReturn(listing3s)
            .doReturn(listing4s)
            .whenever(listingService).search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
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
    fun `aggregate metrics`() {
        // WHEN
        jobs.aggregateMetrics()

        // THEN
        verify(listingMetricService).aggregate(tenants[0].id!!)
        verify(listingMetricService).aggregate(tenants[1].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
    }

    @Test
    fun `aggregate metrics - exception do not stop the process`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .whenever(listingMetricService)
            .aggregate(tenants[1].id!!)

        // WHEN
        jobs.aggregateMetrics()

        // THEN
        verify(listingMetricService).aggregate(tenants[0].id!!)
        verify(listingMetricService).aggregate(tenants[1].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
    }

    @Test
    fun publishing() {
        jobs.publishing()

        val event = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher, times(3)).publish(event.capture())

        assertEquals(ListingStatus.PUBLISHING, event.firstValue.status)
        assertEquals(listing1s[0].id, event.firstValue.listingId)
        assertEquals(listing1s[0].tenantId, event.firstValue.tenantId)

        assertEquals(ListingStatus.PUBLISHING, event.secondValue.status)
        assertEquals(listing1s[1].id, event.secondValue.listingId)
        assertEquals(listing1s[1].tenantId, event.secondValue.tenantId)

        assertEquals(ListingStatus.PUBLISHING, event.thirdValue.status)
        assertEquals(listing2s[0].id, event.thirdValue.listingId)
        assertEquals(listing2s[0].tenantId, event.thirdValue.tenantId)
    }

    @Test
    fun `publishing - exception do no stop the process`() {
        // GIVEN
        // GIVEN
        doAnswer { invocation ->
            {
                val event = invocation.arguments[0] as ListingStatusChangedEvent
                if (event.listingId == listing1s[0].id) {
                    throw RuntimeException()
                }
            }
        }
            .whenever(publisher)
            .publish(any())

        // WHEN
        jobs.publishing()

        // THEN
        val event = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher, times(3)).publish(event.capture())
    }
}
