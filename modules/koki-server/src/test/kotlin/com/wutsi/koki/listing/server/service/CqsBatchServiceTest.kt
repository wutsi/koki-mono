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

class CqsBatchServiceTest {
    private val listingService = mock<ListingService>()
    private val fileService = mock<FileService>()
    private val contentQualityScoreService = mock<ContentQualityScoreService>()
    private val logger = DefaultKVLogger()

    private val service = CqsBatchService(
        listingService = listingService,
        fileService = fileService,
        contentQualityScoreService = contentQualityScoreService,
        logger = logger,
    )

    private val tenantId = 1L

    private val image = FileEntity(id = 1L, tenantId = tenantId)
    private val listing = ListingEntity(
        id = 100L,
        tenantId = tenantId,
        status = ListingStatus.ACTIVE,
        contentQualityScore = null
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
        doReturn(images.size.toLong()).whenever(fileService).countApprovedImages(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
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
    fun `computeAll - updates listings with new CQS`() {
        // GIVEN
        doReturn(75).whenever(contentQualityScoreService).compute(any(), any())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService).save(any(), anyOrNull())
        assertEquals(75, listing.contentQualityScore)
    }

    @Test
    fun `computeAll - skips listings with unchanged CQS`() {
        // GIVEN
        setupListingSearch(listOf(listing.copy(contentQualityScore = 75)))

        val images = listOf(FileEntity(id = 1L, tenantId = tenantId))
        setupImageSearch(images)

        doReturn(75).whenever(contentQualityScoreService).compute(any(), any())

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
                contentQualityScore = null
            )
        }
        val listings2 = (101..150).map { i ->
            ListingEntity(
                id = i.toLong(),
                tenantId = tenantId,
                status = ListingStatus.ACTIVE,
                contentQualityScore = null
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

        doReturn(65).whenever(contentQualityScoreService).compute(any(), any())

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
    fun `computeAll - updates listing with zero CQS when minimal data`() {
        // GIVEN
        doReturn(10).whenever(contentQualityScoreService).compute(any(), any())

        // WHEN
        service.computeAll(tenantId)

        // THEN
        verify(listingService).save(any(), anyOrNull())
        assertEquals(10, listing.contentQualityScore)
    }
}
