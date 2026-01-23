package com.wutsi.koki.listing.server.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.server.service.ListingMetricService
import com.wutsi.koki.tenant.dto.TenantStatus
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class ListingJobsTest {
    private val listingMetricService = mock<ListingMetricService>()
    private val tenantService = mock<TenantService>()
    private val job = ListingJobs(listingMetricService, tenantService)

    private val tenants = listOf(
        TenantEntity(id = 1, name = "Tenant 1", status = TenantStatus.ACTIVE),
        TenantEntity(id = 2, name = "Tenant 2", status = TenantStatus.ACTIVE),
        TenantEntity(id = 3, name = "Tenant 3", status = TenantStatus.NEW),
        TenantEntity(id = 3, name = "Tenant 3", status = TenantStatus.SUSPENDED),
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenants).whenever(tenantService).all()
    }

    @Test
    fun `aggregate all tenants`() {
        // WHEN
        job.aggregateMetrics()

        // THEN
        verify(listingMetricService).aggregate(tenants[0].id!!)
        verify(listingMetricService).aggregate(tenants[1].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
    }

    @Test
    fun `exception do not stop the process`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .whenever(listingMetricService)
            .aggregate(tenants[1].id!!)

        // WHEN
        job.aggregateMetrics()

        // THEN
        verify(listingMetricService).aggregate(tenants[0].id!!)
        verify(listingMetricService).aggregate(tenants[1].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
        verify(listingMetricService, never()).aggregate(tenants[2].id!!)
    }
}
