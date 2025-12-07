package com.wutsi.koki.listing.server.service.similarity

import com.wutsi.koki.listing.server.domain.ListingEntity

/**
 * Strategy interface for computing similarity scores between listings.
 * This allows different similarity algorithms to be implemented and swapped easily.
 */
interface ListingSimilarityStrategy {
    /**
     * Computes the similarity score between two listings.
     *
     * @param reference The reference listing to compare against
     * @param candidate The candidate listing to evaluate
     * @return A similarity score between 0.0 and 1.0, where 1.0 indicates a perfect match
     */
    fun computeSimilarity(reference: ListingEntity, candidate: ListingEntity): Double
}
