package com.wutsi.koki.lodging.dto

data class Room(
    val id: Long = -1,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RootStatus = RootStatus.UNKNOWN,
    val title: String = "",
    val description: String? = null,
    val numberOfRooms: Int? = null,
    val numberOfBathrooms: Int? = null,
    val numberOfBeds: Int? = null,
    val size: Int? = null,
    val maxGuest: Int = -1,

    val cityId: Long = -1,
    val stateId: Long? = -1,
    var street: String? = null,
    val postalCode: String? = null,
    val country: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    val ratings: Int? = null,
    val numberOfRatings: Int = 0,

    val amenityIds: List<Long> = emptyList(),
    val imageFileIds: List<Long> = emptyList(),
)
