package com.wutsi.koki.room.server.mapper

import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.server.domain.RoomEntity
import org.springframework.stereotype.Service

@Service
class RoomMapper {
    fun toRoom(entity: RoomEntity): Room {
        return Room(
            id = entity.id ?: -1,
            accountId = entity.account.id ?: -1,
            heroImageId = entity.heroImageId,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            description = entity.description,
            summary = entity.summary,
            titleFr = entity.titleFr,
            descriptionFr = entity.descriptionFr,
            summaryFr = entity.summaryFr,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            area = entity.area,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.createdById,
            publishedAt = entity.publishedAt,
            publishedById = entity.publishedById,
            neighborhoodId = entity.neighborhoodId,
            address = toAddress(entity),
            pricePerNight = toMoney(entity.pricePerNight, entity.currency),
            pricePerMonth = toMoney(entity.pricePerMonth, entity.currency),
            amenityIds = entity.amenities.map { amenity -> amenity.id },
            checkinTime = entity.checkinTime,
            checkoutTime = entity.checkoutTime,
            longitude = entity.longitude,
            latitude = entity.latitude,
            leaseType = entity.leaseType,
            leaseTerm = entity.leaseTerm,
            furnishedType = entity.furnishedType,
            categoryId = entity.categoryId,
            listingUrl = toListingUrl(entity),
            visitFees = toMoney(entity.visitFees, entity.currency),
            yearOfConstruction = entity.yearOfConstruction,
            dateOfAvailability = entity.dateOfAvailability,
            advanceRent = entity.advanceRent,
            leaseTermDuration = entity.leaseTermDuration,
        )
    }

    fun toRoomSummary(entity: RoomEntity): RoomSummary {
        return RoomSummary(
            id = entity.id ?: -1,
            accountId = entity.account.id ?: -1,
            heroImageId = entity.heroImageId,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            summary = entity.summary,
            titleFr = entity.titleFr,
            summaryFr = entity.summaryFr,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            neighborhoodId = entity.neighborhoodId,
            address = toAddress(entity),
            pricePerNight = toMoney(entity.pricePerNight, entity.currency),
            pricePerMonth = toMoney(entity.pricePerMonth, entity.currency),
            longitude = entity.longitude,
            latitude = entity.latitude,
            listingUrl = toListingUrl(entity),
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            publishedAt = entity.publishedAt,
            area = entity.area,
            leaseType = entity.leaseType,
            leaseTerm = entity.leaseTerm,
            furnishedType = entity.furnishedType,
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

    fun toMoney(amount: Double?, currency: String?): Money? {
        return if (amount == null || currency == null) {
            null
        } else {
            Money(amount, currency)
        }
    }

    fun toListingUrl(entity: RoomEntity): String? {
        return if (entity.status == RoomStatus.PUBLISHED) {
            StringUtils.toSlug("/rooms/${entity.id}", entity.title)
        } else {
            null
        }
    }
}
