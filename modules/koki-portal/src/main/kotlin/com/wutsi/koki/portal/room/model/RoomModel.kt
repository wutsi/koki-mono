package com.wutsi.koki.portal.room.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import java.util.Date

data class RoomModel(
    val id: Long = -1,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String = "",
    val description: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val address: AddressModel = AddressModel(),
    val pricePerNight: MoneyModel = MoneyModel(),
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val modifiedBy: UserModel? = null,
    val readOnly: Boolean = false,
)
