package com.wutsi.koki.listing.server.service

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class AverageImageQualityScoreService {

    companion object {
        /**
         * Maps ImageQuality enum values to their numeric scores.
         */
        private val QUALITY_SCORES = mapOf(
            ImageQuality.UNKNOWN to 0,
            ImageQuality.POOR to 1,
            ImageQuality.LOW to 2,
            ImageQuality.MEDIUM to 3,
            ImageQuality.HIGH to 4
        )
    }

    /**
     * Computes the Average Image Quality Score (AIQS) for a list of images.
     * Only images with status APPROVED are considered in the computation.
     *
     * Formula: AIQS = SUM(IQS) / n
     * - where n is the number of APPROVED images
     * - if n = 0, then AIQS = 0.0
     *
     * @param images List of FileEntity objects (images)
     * @return The AIQS rounded to 2 decimal places (half up)
     */
    fun compute(images: List<FileEntity>): Double {
        val approvedImages = images.filter { image -> image.status == FileStatus.APPROVED }

        if (approvedImages.isEmpty()) {
            return 0.0
        }

        val totalScore = approvedImages.sumOf { image ->
            getScore(image.imageQuality)
        }

        val average = totalScore.toDouble() / approvedImages.size

        return BigDecimal(average)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Returns the numeric score for a given ImageQuality value.
     *
     * @param quality The ImageQuality enum value (can be null)
     * @return The numeric score (0-4)
     */
    fun getScore(quality: ImageQuality?): Int {
        return QUALITY_SCORES[quality ?: ImageQuality.UNKNOWN] ?: 0
    }
}
