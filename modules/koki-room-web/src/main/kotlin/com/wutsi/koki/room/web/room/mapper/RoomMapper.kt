package com.wutsi.koki.room.web.room.mapper

import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.room.web.common.mapper.MoneyMapper
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.common.service.Moment
import com.wutsi.koki.room.web.file.model.FileModel
import com.wutsi.koki.room.web.refdata.model.AddressModel
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.room.model.MapMarkerModel
import com.wutsi.koki.room.web.room.model.RoomModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class RoomMapper(
    private val moneyMapper: MoneyMapper,
    private val moment: Moment
) : TenantAwareMapper() {
    fun toRoomModel(
        entity: RoomSummary,
        images: Map<Long, FileModel>,
        locations: Map<Long, LocationModel>,
        accounts: Map<Long, AccountModel>,
    ): RoomModel {
        val locale = LocaleContextHolder.getLocale()
        return RoomModel(
            id = entity.id,
            account = accounts[entity.accountId] ?: AccountModel(id = entity.accountId),
            type = entity.type,
            status = entity.status,
            title = entity.title,
            summary = entity.summary?.ifEmpty { null },
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            area = if (entity.area > 0) entity.area else null,
            neighborhood = entity.neighborhoodId?.let { id -> locations[id] },
            address = entity.address?.let { address ->
                AddressModel(
                    city = address.cityId?.let { id -> locations[id] },
                    state = address.stateId?.let { id -> locations[id] },
                    street = address.street,
                    postalCode = address.postalCode,
                    country = address.country,
                    countryName = Locale(locale.language, address.country).displayCountry
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
            url = entity.listingUrl ?: "/rooms/${entity.id}",
            publishedAt = entity.publishedAt,
            publishedAtMoment = entity.publishedAt?.let { date -> moment.format(date) },
            leaseType = entity.leaseType,
            leaseTerm = entity.leaseTerm,
            furnishedType = entity.furnishedType,
        )
    }

    fun toRoomModel(
        entity: Room,
        account: AccountModel,
        heroImage: FileModel?,
        locations: Map<Long, LocationModel>,
        images: List<FileModel>,
        amenities: Map<Long, AmenityModel>,
    ): RoomModel {
        val locale = LocaleContextHolder.getLocale()
        val fmtDate = createDateFormat()
        return RoomModel(
            id = entity.id,
            account = account,
            heroImage = heroImage,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            summary = entity.summary?.ifEmpty { null },
            description = entity.description?.ifEmpty { null },
            descriptionHtml = entity.description?.let { text -> HtmlUtils.toHtml(text) },
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            neighborhood = entity.neighborhoodId?.let { id -> locations[id] },
            leaseTerm = entity.leaseTerm,
            leaseType = entity.leaseType,
            furnishedType = entity.furnishedType,
            area = entity.area,
            address = entity.address?.let { address ->
                AddressModel(
                    city = address.cityId?.let { id -> locations[id] },
                    state = address.stateId?.let { id -> locations[id] },
                    street = address.street,
                    postalCode = address.postalCode,
                    country = address.country,
                    countryName = Locale(locale.language, address.country).displayCountry
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
            longitude = entity.longitude,
            latitude = entity.latitude,
            images = images,
            url = entity.listingUrl ?: "/rooms/${entity.id}",
            publishedAt = entity.publishedAt,
            publishedAtMoment = entity.publishedAt?.let { date -> moment.format(date) },
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

    fun toMapMarkerModel(entity: RoomSummary): MapMarkerModel {
        return MapMarkerModel(
            id = entity.id,
            latitude = entity.latitude,
            longitude = entity.longitude,
            price = (entity.pricePerMonth ?: entity.pricePerNight)?.let { money ->
                moneyMapper.toMoneyModel(money.amount, money.currency).toString()
            }
        )
    }
}
