package com.wutsi.koki.listing.server.domain

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_LISTING_LOCATION_METRIC")
data class ListingLocationMetricEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,

    @Column(name = "neighbourhood_fk")
    val neighborhoodId: Long = -1,
    val propertyCategory: PropertyCategory? = null,
    val listingStatus: ListingStatus? = null,
    val listingType: ListingType? = null,

    val totalListings: Int = 0,
    val minPrice: Long = 0L,
    val maxPrice: Long = 0L,
    val averagePrice: Long = 0L,
    val averageLotArea: Int = 0,
    val pricePerSquareMeter: Double = 0.0,
    val totalPrice: Long = 0L,
    val currency: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
