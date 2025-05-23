package com.wutsi.koki.portal.room.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import java.util.Date

data class RoomModel(
    val id: Long = -1,
    val heroImage: FileModel? = null,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val descriptionHtml: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val neighborhood: LocationModel? = null,
    val address: AddressModel? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val pricePerNight: MoneyModel? = null,
    val pricePerMonth: MoneyModel? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val modifiedBy: UserModel? = null,
    val publishedAt: Date? = null,
    val publishedAtText: String? = null,
    val publishedBy: UserModel? = null,
    val amenities: List<AmenityModel> = emptyList(),
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
    var leaseTerm: LeaseTerm = LeaseTerm.UNKNOWN,
    val furnishedType: FurnishedType = FurnishedType.UNKNOWN,
    val leaseType: LeaseType = LeaseType.UNKNOWN,
    val category: CategoryModel? = null,
    val area: Int? = null,
) {
    fun hasAmenity(amenityId: Long): Boolean {
        return amenities.find { amenity -> amenity.id == amenityId } != null
    }

    val draft: Boolean
        get() = (status == RoomStatus.DRAFT)

    val readOnly: Boolean
        get() = !draft

    val hasGeoLocation: Boolean
        get() = (latitude != null && longitude != null)

    val hasPrice: Boolean
        get() = (pricePerNight != null || pricePerMonth != null)
}
