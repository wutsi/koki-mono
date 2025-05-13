package com.wutsi.koki.portal.room.mapper

import com.wutsi.koki.portal.common.HtmlUtils
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.room.model.RoomModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomSummary
import org.springframework.stereotype.Service

@Service
class RoomMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toRoomModel(
        entity: RoomSummary,
        locations: Map<Long, LocationModel>
    ): RoomModel {
        return RoomModel(
            id = entity.id,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            neighborhood = entity.neighborhoodId?.let { id -> locations[id] },
            address = entity.address?.let { address ->
                AddressModel(
                    city = address.cityId?.let { id -> locations[id] },
                    state = address.stateId?.let { id -> locations[id] },
                    street = address.street,
                    postalCode = address.postalCode,
                    country = address.country,
                )
            },
            pricePerNight = moneyMapper.toMoneyModel(entity.pricePerNight.amount, entity.pricePerNight.currency),
        )
    }

    fun toRoomModel(
        entity: Room,
        locations: Map<Long, LocationModel>,
        users: Map<Long, UserModel>,
        amenities: Map<Long, AmenityModel>
    ): RoomModel {
        val fmt = createDateTimeFormat()
        return RoomModel(
            id = entity.id,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            description = entity.description?.ifEmpty { null },
            descriptionHtml = entity.description?.let { text -> HtmlUtils.toHtml(text) },
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            neighborhood = entity.neighborhoodId?.let { id -> locations[id] },
            address = entity.address?.let { address ->
                AddressModel(
                    city = address.cityId?.let { id -> locations[id] },
                    state = address.stateId?.let { id -> locations[id] },
                    street = address.street,
                    postalCode = address.postalCode,
                    country = address.country,
                )
            },
            pricePerNight = moneyMapper.toMoneyModel(entity.pricePerNight.amount, entity.pricePerNight.currency),
            checkinTime = entity.checkinTime,
            checkoutTime = entity.checkoutTime,
            amenities = entity.amenityIds.mapNotNull { id -> amenities[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
        )
    }
}
