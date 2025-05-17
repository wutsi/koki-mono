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
            heroImageId = entity.heroImageId,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            description = entity.description,
            summary = entity.summary,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.createdById,
            publishedAt = entity.publishedAt,
            publishedById = entity.publishedById,
            neighborhoodId = entity.neighborhoodId,
            address = toAddress(entity),
            pricePerNight = Money(
                amount = entity.pricePerNight ?: 0.0,
                currency = (entity.currency ?: "")
            ),
            amenityIds = entity.amenities.map { amenity -> amenity.id },
            checkinTime = entity.checkinTime,
            checkoutTime = entity.checkoutTime,
            longitude = entity.longitude,
            latitude = entity.latitude,
        )
    }

    fun toRoomSummary(entity: RoomEntity): RoomSummary {
        return RoomSummary(
            id = entity.id ?: -1,
            heroImageId = entity.heroImageId,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            summary = entity.summary,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            neighborhoodId = entity.neighborhoodId,
            address = toAddress(entity),
            pricePerNight = Money(
                amount = entity.pricePerNight ?: 0.0,
                currency = (entity.currency ?: "")
            ),
            longitude = entity.longitude,
            latitude = entity.latitude,
        )
    }

    fun toAddress(entity: RoomEntity): Address? {
        return if (entity.hasAddress()) {
            Address(
                cityId = entity.cityId,
                stateId = entity.stateId,
                street = entity.street,
                postalCode = entity.postalCode,
                country = entity.country,
            )
        } else {
            null
        }
    }
}
