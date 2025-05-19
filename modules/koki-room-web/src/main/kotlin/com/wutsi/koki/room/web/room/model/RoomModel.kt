package com.wutsi.koki.room.web.room.model

import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.web.common.model.MoneyModel
import com.wutsi.koki.room.web.file.model.FileModel
import com.wutsi.koki.room.web.refdata.model.AddressModel
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import com.wutsi.koki.room.web.refdata.model.LocationModel

data class RoomModel(
    val id: Long = -1,
    val heroImage: FileModel? = null,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String = "",
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
    val pricePerNight: MoneyModel = MoneyModel(),
    val amenities: List<AmenityModel> = emptyList(),
    val images: List<FileModel> = emptyList(),
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
) {
    fun hasAmenity(amenityId: Long): Boolean {
        return amenities.find { amenity -> amenity.id == amenityId } != null
    }

    fun amenitiesByCategoryId(categoryId: Long): List<AmenityModel> {
        return amenities.filter { amenity -> amenity.categoryId == categoryId }
    }

    val hasGeoLocation: Boolean
        get() = (latitude != null && longitude != null)
}
