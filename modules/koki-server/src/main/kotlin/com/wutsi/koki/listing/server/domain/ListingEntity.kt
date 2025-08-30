package com.wutsi.koki.listing.server.domain

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.refdata.dto.IDType
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_LISTING")
data class ListingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,
    @Column(name = "created_by_fk") val createdById: Long? = null,
    @Column(name = "modified_by_fk") var modifiedById: Long? = null,
    @Column("hero_image_fk") var heroImageId: Long? = null,

    var status: ListingStatus = ListingStatus.UNKNOWN,
    val listingNumber: Long = -1,
    var listingType: ListingType? = null,
    var propertyType: PropertyType? = null,
    var bedrooms: Int? = null,
    var bathrooms: Int? = null,
    var halfBathrooms: Int? = null,
    var floors: Int? = null,
    var basementType: BasementType? = null,
    var level: Int? = null,
    var unit: String? = null,
    var parkingType: ParkingType? = null,
    var parkings: Int? = null,
    var fenceType: FenceType? = null,
    var lotArea: Int? = null,
    var propertyArea: Int? = null,
    var year: Int? = null,

    @ManyToMany
    @JoinTable(
        name = "T_LISTING_AMENITY",
        joinColumns = arrayOf(JoinColumn(name = "listing_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "amenity_fk")),
    )
    var amenities: MutableList<AmenityEntity> = mutableListOf(),
    var furnitureType: FurnitureType? = null,

    @Column("city_fk") var cityId: Long? = null,
    @Column("state_fk") var stateId: Long? = null,
    @Column("neighbourhood_fk") var neighbourhoodId: Long? = null,
    var street: String? = null,
    var postalCode: String? = null,
    var country: String? = null,

    var latitude: Double? = null,
    var longitude: Double? = null,

    var agentRemarks: String? = null,
    var publicRemarks: String? = null,

    var price: Long? = null,
    var visitFees: Long? = null,
    var currency: String? = null,
    var sellerAgentCommission: Double? = null,
    var buyerAgentCommission: Double? = null,

    var securityDeposit: Long? = null,
    var advanceRent: Int? = null,
    var leaseTerm: Int? = null,
    var noticePeriod: Int? = null,

    var sellerName: String? = null,
    var sellerPhone: String? = null,
    var sellerEmail: String? = null,
    var sellerIdNumber: String? = null,
    var sellerIdType: IDType? = null,
    var sellerIdCountry: String? = null,
    @Column("seller_agent_user_fk") var sellerAgentUserId: Long? = null,

    var title: String? = null,
    var summary: String? = null,
    var description: String? = null,
    var titleFr: String? = null,
    var summaryFr: String? = null,
    var descriptionFr: String? = null,

    var buyerName: String? = null,
    var buyerEmail: String? = null,
    var buyerPhone: String? = null,
    var transactionDate: Date? = null,
    var transactionPrice: Long? = null,
    @Column("buyer_agent_user_fk") var buyerAgentUserId: Long? = null,

    var totalImages: Long? = null,
    var totalFiles: Long? = null,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var publishedAt: Date? = null,
    var closedAt: Date? = null,
)
