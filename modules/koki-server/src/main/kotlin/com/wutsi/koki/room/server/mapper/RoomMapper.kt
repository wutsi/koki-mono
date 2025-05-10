package com.wutsi.koki.room.server.mapper

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.server.domain.RoomEntity
import org.springframework.stereotype.Service

@Service
class RoomMapper {
    fun toRoom(entity: RoomEntity): Room {
        return Room(
            id = entity.id ?: -1,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            description = entity.description,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.createdById,
            address = Address(
                cityId = entity.cityId,
                stateId = entity.stateId,
                street = entity.street,
                postalCode = entity.postalCode,
                country = entity.country,
            ),
            pricePerNight = Money(
                amount = entity.pricePerNight ?: 0.0,
                currency = (entity.currency ?: "")
            ),
            amenityIds = entity.amenities.map { amenity -> amenity.id },
        )
    }

    fun toRoomSummary(entity: RoomEntity): RoomSummary {
        return RoomSummary(
            id = entity.id ?: -1,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            pricePerNight = Money(
                amount = entity.pricePerNight ?: 0.0,
                currency = (entity.currency ?: "")
            ),
            address = Address(
                cityId = entity.cityId,
                stateId = entity.stateId,
                street = entity.street,
                postalCode = entity.postalCode,
                country = entity.country,
            ),
        )
    }
}
