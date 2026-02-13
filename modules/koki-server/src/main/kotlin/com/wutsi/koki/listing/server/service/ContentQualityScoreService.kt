package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.ContentQualityScoreBreakdown
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.server.domain.ListingEntity
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ContentQualityScoreService {

    companion object {
        private const val MAX_GENERAL_SCORE = 20
        private const val MAX_LEGAL_SCORE = 10
        private const val MAX_AMENITIES_SCORE = 10
        private const val MAX_ADDRESS_SCORE = 5
        private const val MAX_GEO_SCORE = 15
        private const val MAX_RENTAL_SCORE = 10
        private const val MAX_IMAGES_SCORE = 30
        private const val MAX_IMAGE_COUNT = 20
    }

    /**
     * Computes the Content Quality Score (CQS) for a listing.
     *
     * @param listing The listing entity
     * @param validImageCount Number of approved images
     * @return The total CQS (0-100)
     */
    fun compute(listing: ListingEntity, validImageCount: Int): Int {
        val breakdown = computeBreakdown(listing, validImageCount)
        return breakdown.total
    }

    /**
     * Computes the detailed breakdown of the Content Quality Score.
     *
     * @param listing The listing entity
     * @param validImageCount Number of approved images
     * @return ContentQualityScoreBreakdown with all component scores
     */
    fun computeBreakdown(listing: ListingEntity, validImageCount: Int): ContentQualityScoreBreakdown {
        val general = computeGeneralScore(listing)
        val legal = computeLegalScore(listing)
        val amenities = computeAmenitiesScore(listing)
        val address = computeAddressScore(listing)
        val geo = computeGeoScore(listing)
        val rental = computeRentalScore(listing)
        val images = computeImagesScore(validImageCount, listing.averageImageQualityScore)
        val total = general + legal + amenities + address + geo + rental + images

        return ContentQualityScoreBreakdown(
            general = general,
            legal = legal,
            amenities = amenities,
            address = address,
            geo = geo,
            rental = rental,
            images = images,
            total = total,
        )
    }

    /**
     * Computes the general information score (0-20).
     * Score varies by property category:
     * - Residential: 12 fields
     * - Land: 5 fields
     * - Commercial: 10 fields
     */
    private fun computeGeneralScore(listing: ListingEntity): Int {
        val category = listing.propertyCategory ?: return 0

        val fields = when (category) {
            PropertyCategory.RESIDENTIAL -> listOf(
                listing.propertyType,
                listing.bedrooms,
                listing.bathrooms,
                listing.halfBathrooms,
                listing.floors,
                listing.basementType,
                listing.level,
                listing.parkingType,
                listing.parkings,
                listing.propertyArea,
                listing.year,
                listing.furnitureType,
            )

            PropertyCategory.LAND -> listOf(
                listing.propertyType,
                listing.lotArea,
                listing.fenceType,
                listing.roadPavement,
                listing.distanceFromMainRoad,
            )

            PropertyCategory.COMMERCIAL -> listOf(
                listing.propertyType,
                listing.floors,
                listing.level,
                listing.parkingType,
                listing.parkings,
                listing.propertyArea,
                listing.year,
                listing.units,
                listing.revenue,
                listing.furnitureType,
            )

            else -> emptyList()
        }

        val filledCount = fields.count { it != null }
        val totalFields = fields.size
        if (totalFields == 0) return 0

        return (filledCount.toDouble() / totalFields * MAX_GENERAL_SCORE).toInt()
    }

    /**
     * Computes the legal information score (0-10).
     * Based on 6 fields: landTitle, technicalFile, subdivided, morcelable, numberOfSigners, transactionWithNotary
     */
    private fun computeLegalScore(listing: ListingEntity): Int {
        val fields = listOf(
            listing.landTitle,
            listing.technicalFile,
            listing.subdivided,
            listing.morcelable,
            listing.numberOfSigners,
            listing.transactionWithNotary,
        )

        val filledCount = fields.count { it != null }
        val totalFields = fields.size

        return (filledCount.toDouble() / totalFields * MAX_LEGAL_SCORE).toInt()
    }

    /**
     * Computes the amenities score (0-10).
     * Score = MIN(amenities.size, 10)
     */
    private fun computeAmenitiesScore(listing: ListingEntity): Int {
        return min(listing.amenities.size, MAX_AMENITIES_SCORE)
    }

    /**
     * Computes the address score (0-5).
     * Based on 4 fields: street, neighbourhoodId, cityId, country
     */
    private fun computeAddressScore(listing: ListingEntity): Int {
        val fields = listOf(
            listing.street,
            listing.neighbourhoodId,
            listing.cityId,
            listing.country,
        )

        val filledCount = fields.count { it != null }
        val totalFields = fields.size

        return (filledCount.toDouble() / totalFields * MAX_ADDRESS_SCORE).toInt()
    }

    /**
     * Computes the geo location score (0-15).
     * Returns 15 if both latitude and longitude are present, otherwise 0
     */
    private fun computeGeoScore(listing: ListingEntity): Int {
        return if (listing.latitude != null && listing.longitude != null) {
            MAX_GEO_SCORE
        } else {
            0
        }
    }

    /**
     * Computes the rental information score (0-10).
     * For rentals: based on 4 fields (leaseTerm, advanceRent, securityDeposit, noticePeriod)
     * For non-rentals: returns 10
     */
    private fun computeRentalScore(listing: ListingEntity): Int {
        val isRental = listing.listingType == ListingType.RENTAL

        if (!isRental) {
            return MAX_RENTAL_SCORE
        }

        val fields = listOf(
            listing.leaseTerm,
            listing.advanceRent,
            listing.securityDeposit,
            listing.noticePeriod,
        )

        val filledCount = fields.count { it != null }
        val totalFields = fields.size

        return (filledCount.toDouble() / totalFields * MAX_RENTAL_SCORE).toInt()
    }

    /**
     * Computes the images score (0-30).
     * Formula: MIN(validImageCount, 20) / 20 * 30 * (AIQS / 4.0)
     * Returns 0 if no images or AIQS is null/0
     */
    private fun computeImagesScore(validImageCount: Int, aiqs: Double?): Int {
        if (validImageCount == 0 || aiqs == null || aiqs == 0.0) {
            return 0
        }

        val cappedImageCount = min(validImageCount, MAX_IMAGE_COUNT)
        val score = (cappedImageCount.toDouble() / MAX_IMAGE_COUNT) * MAX_IMAGES_SCORE * (aiqs / 4.0)

        return score.toInt()
    }
}
