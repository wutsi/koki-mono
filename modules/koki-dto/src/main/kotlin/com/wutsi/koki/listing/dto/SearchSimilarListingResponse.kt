package com.wutsi.koki.listing.dto

/**
 * Response containing a list of similar listings sorted by similarity score in descending order.
 *
 * @property listings List of similar listings with their similarity scores
 */
data class SearchSimilarListingResponse(
    val listings: List<ListingSimilaritySummary> = emptyList()
)
