package com.wutsi.koki.listing.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class AiqsBatchServiceTest {
    private val listingService = mock<ListingService>()
    private val fileService = mock<FileService>()
    private val averageImageQualityScoreService = mock<AverageImageQualityScoreService>()
    private val logger = DefaultKVLogger()

    private val service = AiqsBatchService(
        listingService = listingService,
        fileService = fileService,
        averageImageQualityScoreService = averageImageQualityScoreService,
        logger = logger,
    )

    private val tenantId = 1L

    private val image = FileEntity(id = 1L, tenantId = tenantId)
    private val listing = ListingEntity(
        id = 100L,
        tenantId = tenantId,
        status = ListingStatus.ACTIVE,
        averageImageQualityScore = null
    )

    @BeforeEach
    fun setUp() {
        setupListingSearch(listOf(listing))

        setupImageSearch(listOf(image))
    }

    private fun setupListingSearch(listings: List<ListingEntity>) {
        doReturn(listings).whenever(listingService).search(
            any(),
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

    private fun setupImageSearch(images: List<FileEntity>) {
        doReturn(images).whenever(fileService).search(
            any(),
            anyOrNull(),
            eq(listing.id),
            eq(ObjectType.LISTING),
            eq(FileType.IMAGE),
            eq(FileStatus.APPROVED),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun `computeAll - updates listings with new AIQS`() {
        // GIVEN
        doReturn(3.5).whenever(averageImageQualityScoreService).compute(any())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService).save(any(), anyOrNull())
        assertEquals(3.5, listing.averageImageQualityScore)
    }

    @Test
    fun `computeAll - skips listings with unchanged AIQS`() {
        // GIVEN
        setupListingSearch(listOf(listing.copy(averageImageQualityScore = 3.5)))

        val images = listOf(FileEntity(id = 1L, tenantId = tenantId))
        setupImageSearch(images)

        doReturn(3.5).whenever(averageImageQualityScoreService).compute(any())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `computeAll - processes multiple batches`() {
        // GIVEN
        val listings1 = (1..100).map { i ->
            ListingEntity(
                id = i.toLong(),
                tenantId = tenantId,
                status = ListingStatus.ACTIVE,
                averageImageQualityScore = null
            )
        }
        val listings2 = (101..150).map { i ->
            ListingEntity(
                id = i.toLong(),
                tenantId = tenantId,
                status = ListingStatus.ACTIVE,
                averageImageQualityScore = null
            )
        }

        doReturn(listings1).doReturn(listings2)
            .doReturn(emptyList<ListingEntity>())
            .whenever(listingService).search(
                any(),
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

        doReturn(2.5).whenever(averageImageQualityScoreService).compute(any())

        // WHEN
        service.computeAll(tenantId)

        // Verify save was called for all 150 listings
        verify(listingService, times(150)).save(any(), anyOrNull())
    }

    @Test
    fun `computeAll - no listings found`() {
        // GIVEN
        setupListingSearch(emptyList())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `computeAll - updates listing with zero AIQS when no approved images`() {
        // GIVEN
        doReturn(0.0).whenever(averageImageQualityScoreService).compute(any())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService).save(any(), anyOrNull())
        assertEquals(0.0, listing.averageImageQualityScore)
    }
}
