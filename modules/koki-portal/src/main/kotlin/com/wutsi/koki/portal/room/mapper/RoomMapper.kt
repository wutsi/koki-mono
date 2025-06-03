package com.wutsi.koki.portal.room.mapper

import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
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
        accounts: Map<Long, AccountModel>,
        locations: Map<Long, LocationModel>,
        images: Map<Long, FileModel>,
    ): RoomModel {
        return RoomModel(
            id = entity.id,
            account = accounts[entity.accountId] ?: AccountModel(id = entity.accountId),
            type = entity.type,
            status = entity.status,
            title = entity.title?.ifEmpty { null },
            summary = entity.summary?.ifEmpty { null },
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
            pricePerNight = entity.pricePerNight?.let { price ->
                moneyMapper.toMoneyModel(
                    price.amount,
                    price.currency
                )
            },
            pricePerMonth = entity.pricePerMonth?.let { price ->
                moneyMapper.toMoneyModel(
                    price.amount,
                    price.currency
                )
            },
            heroImage = entity.heroImageId?.let { id -> images[id] },
            longitude = entity.longitude,
            latitude = entity.latitude,
            area = entity.area,
            listingUrl = entity.listingUrl,
            leaseType = entity.leaseType,
            leaseTerm = entity.leaseTerm,
            furnishedType = entity.furnishedType,
        )
    }

    fun toRoomModel(
        entity: Room,
        account: AccountModel,
        locations: Map<Long, LocationModel>,
        users: Map<Long, UserModel>,
        amenities: Map<Long, AmenityModel>,
        image: FileModel?,
        category: CategoryModel?,
    ): RoomModel {
        val fmt = createDateTimeFormat()
        val fmtDate = createDateFormat()
        return RoomModel(
            id = entity.id,
            account = account,
            heroImage = image,
            type = entity.type,
            status = entity.status,
            title = entity.title?.ifEmpty { null },
            summary = entity.summary?.ifEmpty { null },
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
            pricePerNight = entity.pricePerNight?.let { price ->
                moneyMapper.toMoneyModel(
                    price.amount,
                    price.currency
                )
            },
            pricePerMonth = entity.pricePerMonth?.let { price ->
                moneyMapper.toMoneyModel(
                    price.amount,
                    price.currency
                )
            },
            checkinTime = entity.checkinTime,
            checkoutTime = entity.checkoutTime,
            amenities = entity.amenityIds.mapNotNull { id -> amenities[id] },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            publishedAt = entity.publishedAt,
            publishedAtText = entity.publishedAt?.let { date -> fmt.format(date) },
            publishedBy = entity.publishedById?.let { id -> users[id] },
            longitude = entity.longitude,
            latitude = entity.latitude,
            leaseTerm = entity.leaseTerm,
            leaseType = entity.leaseType,
            category = category,
            furnishedType = entity.furnishedType,
            area = entity.area,
            listingUrl = entity.listingUrl,
            visitFees = entity.visitFees?.let { price ->
                moneyMapper.toMoneyModel(
                    price.amount,
                    price.currency
                )
            },
            yearOfConstruction = entity.yearOfConstruction,
            dateOfAvailability = entity.dateOfAvailability,
            dateOfAvailabilityText = entity.dateOfAvailability?.let { date -> fmtDate.format(date) },
            advanceRent = entity.advanceRent,
            leaseTermDuration = entity.leaseTermDuration,
        )
    }
}
