package com.wutsi.koki.listing.server.service

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.server.domain.FileEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AverageImageQualityScoreServiceTest {

    private val service = AverageImageQualityScoreService()

    // Test cases for compute() method

    @Test
    fun `compute - empty list returns 0`() {
        val result = service.compute(emptyList())
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - single HIGH quality APPROVED image returns 4`() {
        val images = listOf(createImage(ImageQuality.HIGH, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - single UNKNOWN quality APPROVED image returns 0`() {
        val images = listOf(createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - single null quality APPROVED image returns 0`() {
        val images = listOf(createImage(null, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed qualities APPROVED returns correct average`() {
        // HIGH(4) + MEDIUM(3) + LOW(2) = 9 / 3 = 3.0
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    @Test
    fun `compute - rounds to 2 decimal places half up`() {
        // HIGH(4) + MEDIUM(3) + LOW(2) + POOR(1) = 10 / 4 = 2.5
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(2.5, result)
    }

    @Test
    fun `compute - rounding half up - 2_666 becomes 2_67`() {
        // HIGH(4) + LOW(2) + LOW(2) = 8 / 3 = 2.666... → 2.67
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(2.67, result)
    }

    @Test
    fun `compute - all POOR quality APPROVED images returns 1`() {
        val images = listOf(
            createImage(ImageQuality.POOR, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(1.0, result)
    }

    @Test
    fun `compute - all HIGH quality APPROVED images returns 4`() {
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - all UNKNOWN quality APPROVED images returns 0`() {
        val images = listOf(
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED),
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed with null and UNKNOWN APPROVED returns correct average`() {
        // null(0) + UNKNOWN(0) + HIGH(4) = 4 / 3 = 1.33
        val images = listOf(
            createImage(null, FileStatus.APPROVED),
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(1.33, result)
    }

    // Test cases for filtering by APPROVED status

    @Test
    fun `compute - ignores REJECTED images`() {
        // Only HIGH(4) is APPROVED, MEDIUM is REJECTED
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.REJECTED)
        )
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - ignores UNDER_REVIEW images`() {
        // Only LOW(2) is APPROVED, HIGH is UNDER_REVIEW
        val images = listOf(
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.UNDER_REVIEW)
        )
        val result = service.compute(images)
        assertEquals(2.0, result)
    }

    @Test
    fun `compute - ignores UNKNOWN status images`() {
        // Only MEDIUM(3) is APPROVED, POOR has UNKNOWN status
        val images = listOf(
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.UNKNOWN)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    @Test
    fun `compute - returns 0 when all images are REJECTED`() {
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.REJECTED),
            createImage(ImageQuality.HIGH, FileStatus.REJECTED)
        )
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed statuses only counts APPROVED`() {
        // APPROVED: HIGH(4) + LOW(2) = 6 / 2 = 3.0
        // REJECTED and UNDER_REVIEW are ignored
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.REJECTED),
            createImage(ImageQuality.HIGH, FileStatus.UNDER_REVIEW),
            createImage(ImageQuality.HIGH, FileStatus.UNKNOWN)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    // Test cases for getScore() method

    @Test
    fun `getScore - UNKNOWN returns 0`() {
        assertEquals(0, service.getScore(ImageQuality.UNKNOWN))
    }

    @Test
    fun `getScore - POOR returns 1`() {
        assertEquals(1, service.getScore(ImageQuality.POOR))
    }

    @Test
    fun `getScore - LOW returns 2`() {
        assertEquals(2, service.getScore(ImageQuality.LOW))
    }

    @Test
    fun `getScore - MEDIUM returns 3`() {
        assertEquals(3, service.getScore(ImageQuality.MEDIUM))
    }

    @Test
    fun `getScore - HIGH returns 4`() {
        assertEquals(4, service.getScore(ImageQuality.HIGH))
    }

    @Test
    fun `getScore - null returns 0`() {
        assertEquals(0, service.getScore(null))
    }

    // Helper method

    private fun createImage(quality: ImageQuality?, status: FileStatus = FileStatus.APPROVED): FileEntity {
        return FileEntity(
            id = System.nanoTime(),
            tenantId = 1L,
            imageQuality = quality,
            status = status
        )
    }
}
