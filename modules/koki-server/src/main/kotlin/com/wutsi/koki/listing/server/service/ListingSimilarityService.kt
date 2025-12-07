package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.similarity.ListingSimilarityStrategy
import org.springframework.stereotype.Service

/**
 * Service for finding similar listings based on various criteria.
 */
@Service
class ListingSimilarityService(
    private val listingService: ListingService,
    private val similarityStrategy: ListingSimilarityStrategy,
) {
    companion object {
        private val EXCLUSIVE_TYPES = setOf(
            PropertyType.LAND,
            PropertyType.COMMERCIAL,
            PropertyType.INDUSTRIAL
        )
    }

    /**
     * Finds similar listings for a given reference listing.
     *
     * @param referenceId The ID of the reference listing
     * @param tenantId The tenant ID
     * @param statuses Optional filter for listing statuses
     * @param sameAgent If true, only return listings with the same seller agent
     * @param sameNeighborhood If true, only return listings in the same neighborhood
     * @param sameCity If true, only return listings in the same city
     * @param limit Maximum number of results to return
     * @return List of similar listing IDs with their similarity scores, sorted by score descending
     */
    fun findSimilar(
        referenceId: Long,
        tenantId: Long,
        statuses: List<ListingStatus> = emptyList(),
        sameAgent: Boolean = false,
        sameNeighborhood: Boolean = false,
        sameCity: Boolean = false,
        limit: Int = 10,
    ): List<Pair<Long, Double>> {
        // Get the reference listing
        val reference = listingService.get(referenceId, tenantId)

        // Find candidate listings
        val candidates = findCandidates(
            reference = reference,
            tenantId = tenantId,
            statuses = statuses,
            sameAgent = sameAgent,
            sameNeighborhood = sameNeighborhood,
            sameCity = sameCity
        )

        // Compute similarity scores
        val scoredListings = candidates
            .filter { it.id != referenceId } // Exclude the reference itself
            .mapNotNull { candidate ->
                val score = similarityStrategy.computeSimilarity(reference, candidate)
                if (score > 0.0) {
                    candidate.id!! to score
                } else {
                    null
                }
            }
            .sortedByDescending { it.second }
            .take(limit)

        return scoredListings
    }

    /**
     * Finds candidate listings that match the hard filtering criteria.
     * Reuses ListingService.search() to leverage existing filtering logic.
     */
    private fun findCandidates(
        reference: ListingEntity,
        tenantId: Long,
        statuses: List<ListingStatus>,
        sameAgent: Boolean,
        sameNeighborhood: Boolean,
        sameCity: Boolean,
    ): List<ListingEntity> {
        // Determine property types to search for
        val propertyTypes = determinePropertyTypes(reference.propertyType)

        // Calculate bedroom range (±1 for non-exclusive types)
        val (minBedrooms, maxBedrooms) = calculateBedroomRange(reference)

        // Calculate price range (±25%)
        val refPrice = reference.salePrice ?: reference.price
        val (minPrice, maxPrice, minSalePrice, maxSalePrice) = calculatePriceRange(refPrice)

        // Determine location IDs for filtering
        val locationIds = mutableListOf<Long>()
        if (sameCity) {
            reference.cityId?.let { locationIds.add(it) }
        }
        if (sameNeighborhood) {
            reference.neighbourhoodId?.let { locationIds.add(it) }
        }

        // Use ListingService.search() with all calculated parameters
        return listingService.search(
            tenantId = tenantId,
            propertyTypes = propertyTypes,
            statuses = statuses,
            minBedrooms = minBedrooms,
            maxBedrooms = maxBedrooms,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minSalePrice = minSalePrice,
            maxSalePrice = maxSalePrice,
            sellerAgentUserId = if (sameAgent) reference.sellerAgentUserId else null,
            locationIds = locationIds,
            limit = 200, // Get more candidates for scoring
            offset = 0
        )
    }

    /**
     * Determines which property types to search for based on the reference type.
     */
    private fun determinePropertyTypes(refType: PropertyType?): List<PropertyType> {
        if (refType == null) {
            return emptyList()
        }

        return if (EXCLUSIVE_TYPES.contains(refType)) {
            // LAND, COMMERCIAL, INDUSTRIAL only match themselves
            listOf(refType)
        } else {
            // Other types match any type that's not LAND, COMMERCIAL, or INDUSTRIAL
            PropertyType.entries
                .filter { !EXCLUSIVE_TYPES.contains(it) && it != PropertyType.UNKNOWN }
        }
    }

    /**
     * Calculates bedroom range (±1) for non-exclusive property types.
     */
    private fun calculateBedroomRange(reference: ListingEntity): Pair<Int?, Int?> {
        val refType = reference.propertyType
        val refBedrooms = reference.bedrooms

        return if (refType != null && !EXCLUSIVE_TYPES.contains(refType) && refBedrooms != null) {
            Pair(refBedrooms - 1, refBedrooms + 1)
        } else {
            Pair(null, null)
        }
    }

    /**
     * Calculates price range (±25%) for both regular price and sale price.
     */
    private fun calculatePriceRange(refPrice: Long?): Tuple4<Long?, Long?, Long?, Long?> {
        return if (refPrice != null && refPrice > 0) {
            val minPrice = (refPrice * 0.75).toLong()
            val maxPrice = (refPrice * 1.25).toLong()
            Tuple4(minPrice, maxPrice, minPrice, maxPrice)
        } else {
            Tuple4(null, null, null, null)
        }
    }

    private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
