package com.wutsi.koki.room.server.domain

import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
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
@Table(name = "T_ROOM")
data class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "account_fk")
    val accountId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deleteById: Long? = null,

    @Column(name = "published_by_fk")
    var publishedById: Long? = null,

    var type: RoomType = RoomType.UNKNOWN,
    var status: RoomStatus = RoomStatus.UNKNOWN,
    var deleted: Boolean = false,
    var title: String? = null,
    var summary: String? = null,
    var description: String? = null,
    var numberOfRooms: Int = 0,
    var numberOfBathrooms: Int = 0,
    var numberOfBeds: Int = 0,
    var maxGuests: Int = 0,
    var area: Int = 0,
    var checkinTime: String? = null,
    var checkoutTime: String? = null,

    var pricePerNight: Double? = null,
    var pricePerMonth: Double? = null,
    var currency: String? = null,

    @Column("city_fk") var cityId: Long? = null,
    @Column("state_fk") var stateId: Long? = null,
    @Column("neighborhood_fk") var neighborhoodId: Long? = null,
    var street: String? = null,
    var postalCode: String? = null,
    var country: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var leaseTerm: LeaseTerm = LeaseTerm.UNKNOWN,
    var furnishedType: FurnishedType = FurnishedType.UNKNOWN,
    var leaseType: LeaseType = LeaseType.UNKNOWN,

    @Column("category_fk")
    var categoryId: Long? = null,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var publishedAt: Date? = null,

    @Column("hero_image_fk")
    var heroImageId: Long? = null,
    var heroImageReason: String? = null,

    @ManyToMany
    @JoinTable(
        name = "T_ROOM_AMENITY",
        joinColumns = arrayOf(JoinColumn(name = "room_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "amenity_fk")),
    )
    var amenities: MutableList<AmenityEntity> = mutableListOf(),
) {
    fun hasAddress(): Boolean {
        return cityId != null ||
            stateId != null ||
            !postalCode.isNullOrEmpty() ||
            !street.isNullOrEmpty() ||
            !country.isNullOrEmpty()
    }
}
