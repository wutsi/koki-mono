package com.wutsi.koki.listing.server.domain

import java.util.Date

/**
 * IMPORTANT: This is not persisted in the DB, but in S3
 */
data class AIListingEntity(
    val id: Long? = null,
    val tenantId: Long = -1,
    val listing: ListingEntity = ListingEntity(),
    val text: String = "",
    val result: String = "",
    val createdAt: Date = Date(),
)
