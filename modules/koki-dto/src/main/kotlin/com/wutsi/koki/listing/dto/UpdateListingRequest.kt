package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotNull
import java.util.Date

data class UpdateListingRequest(
    @get:NotNull val listingType: ListingType? = null,
    @get:NotNull val propertyType: PropertyType? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val halfBathrooms: Int? = null,
    val floors: Int? = null,
    val basementType: BasementType? = null,
    val level: Int? = null,
    val unit: String? = null,
    val parkingType: ParkingType? = null,
    val parkings: Int? = null,
    val fenceType: FenceType? = null,
    val lotArea: Int? = null,
    val propertyArea: Int? = null,
    val year: Int? = null,
    val availableAt: Date? = null,
    val roadPavement: RoadPavement? = null,
    val distanceFromMainRoad: Int? = null,
    val units: Int? = null,
    val revenue: Long? = null,
)
