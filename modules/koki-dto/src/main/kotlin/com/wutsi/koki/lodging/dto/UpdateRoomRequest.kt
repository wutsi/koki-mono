package com.wutsi.koki.lodging.dto

import com.wutsi.koki.refdata.dto.Address
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateRoomRequest(
    val type: RoomType = RoomType.UNKNOWN,
    val status: RootStatus = RootStatus.UNKNOWN,
    @get:NotEmpty @get:Size(max = 100) val title: String = "",
    val description: String? = null,
    val numberOfRooms: Int? = null,
    val numberOfBathrooms: Int? = null,
    val numberOfBeds: Int? = null,
    val size: Int? = null,
    val maxGuest: Int = -1,
    val address: Address? = null,
)
