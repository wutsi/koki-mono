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
@Table(name = "T_LISTING_METRIC")
data class ListingMetricEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,

    @Column(name = "neighbourhood_fk") val neighborhoodId: Long? = null,
    @Column(name = "seller_agent_user_fk") val sellerAgentUserId: Long? = null,
    @Column(name = "city_fk") val cityId: Long? = null,
    val bedrooms: Int? = null,

    val propertyCategory: PropertyCategory = PropertyCategory.UNKNOWN,
    val listingStatus: ListingStatus = ListingStatus.UNKNOWN,
    val listingType: ListingType = ListingType.UNKNOWN,
    val totalListings: Int = 0,
    val minPrice: Long = 0L,
    val maxPrice: Long = 0L,
    val averagePrice: Long = 0L,
    val averageLotArea: Int? = null,
    val pricePerSquareMeter: Long? = null,
    val totalPrice: Long = 0L,
    val currency: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
