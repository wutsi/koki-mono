package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotNull

data class UpdateListingAmenitiesRequest(
    @get:NotNull val furnitureType: FurnitureType? = null,
    val amenityIds: List<Long> = emptyList()
)
