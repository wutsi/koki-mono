package com.wutsi.koki.listing.server.service.similarity

import com.wutsi.koki.listing.server.domain.ListingEntity
import org.springframework.stereotype.Component
import kotlin.math.abs

/**
 * Default implementation of the listing similarity strategy.
 * Uses a weighted scoring algorithm based on property type, number of bedrooms, and price.
 *
 * Weights:
 * - Property Type: 50%
 * - Number of Bedrooms: 30%
 * - Price: 20%
 */
@Component
class DefaultListingSimilarityStrategy : ListingSimilarityStrategy {
    companion object {
        private const val WEIGHT_TYPE = 0.5
        private const val WEIGHT_BEDROOMS = 0.3
        private const val WEIGHT_PRICE = 0.2
        private const val PRICE_TOLERANCE = 0.25 // ±25%
    }

    override fun computeSimilarity(reference: ListingEntity, candidate: ListingEntity): Double {
        // 1. Type Score
        val typeScore = computeTypeScore(reference, candidate)

        // 2. Bedroom Score
        val bedroomScore = computeBedroomScore(reference, candidate)

        // 3. Price Score
        val priceScore = computePriceScore(reference, candidate)

        // 4. Weighted Total
        return (WEIGHT_TYPE * typeScore) + (WEIGHT_BEDROOMS * bedroomScore) + (WEIGHT_PRICE * priceScore)
    }

    /**
     * Computes the type similarity score.
     * Returns 1.0 if types match, 0.0 otherwise.
     */
    private fun computeTypeScore(reference: ListingEntity, candidate: ListingEntity): Double {
        val refType = reference.propertyType ?: return 0.0
        val candType = candidate.propertyType ?: return 0.0

        return if (refType == candType) 1.0 else 0.0
    }

    /**
     * Computes the bedroom similarity score.
     * - 0 difference: 1.0
     * - 1 difference: 0.5
     * - 2+ difference: 0.0
     */
    private fun computeBedroomScore(reference: ListingEntity, candidate: ListingEntity): Double {
        val refBeds = reference.bedrooms ?: return 0.0
        val candBeds = candidate.bedrooms ?: return 0.0

        val diff = abs(refBeds - candBeds)
        return when (diff) {
            0 -> 1.0
            1 -> 0.5
            else -> 0.0
        }
    }

    /**
     * Computes the price similarity score.
     * Uses linear decay from 1.0 at perfect match to 0.0 at ±25% boundary.
     * Returns 0.0 if outside the ±25% range.
     */
    private fun computePriceScore(reference: ListingEntity, candidate: ListingEntity): Double {
        val refPrice = getEffectivePrice(reference) ?: return 0.0
        val candPrice = getEffectivePrice(candidate) ?: return 0.0

        if (refPrice <= 0) return 0.0

        // Check if within ±25% boundary
        val lowerBound = refPrice * (1 - PRICE_TOLERANCE)
        val upperBound = refPrice * (1 + PRICE_TOLERANCE)

        if (candPrice < lowerBound || candPrice > upperBound) {
            return 0.0
        }

        // Linear decay: normalize the difference
        val priceDiffNorm = abs(refPrice - candPrice) / (refPrice * PRICE_TOLERANCE)
        return 1.0 - priceDiffNorm
    }

    /**
     * Gets the effective price for a listing.
     * For sold/closed listings, uses sale price if available, otherwise regular price.
     */
    private fun getEffectivePrice(listing: ListingEntity): Double? {
        return (listing.salePrice ?: listing.price)?.toDouble()
    }
}
