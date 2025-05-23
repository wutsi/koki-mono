package com.wutsi.koki.room.web.room.mapper

import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.web.common.mapper.MoneyMapper
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.common.util.HtmlUtils
import com.wutsi.koki.room.web.common.util.StringUtils
import com.wutsi.koki.room.web.file.model.FileModel
import com.wutsi.koki.room.web.refdata.model.AddressModel
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.room.model.RoomModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class RoomMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toRoomModel(
        entity: RoomSummary,
        heroImage: FileModel?,
        locations: Map<Long, LocationModel>,
    ): RoomModel {
        val locale = LocaleContextHolder.getLocale()
        return RoomModel(
            id = entity.id,
            type = entity.type,
            status = entity.status,
            title = entity.title,
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
            heroImage = heroImage,
            longitude = entity.longitude,
            latitude = entity.latitude,
        )
    }

    fun toRoomModel(
        entity: Room,
        heroImage: FileModel?,
        locations: Map<Long, LocationModel>,
        images: List<FileModel>,
        amenities: Map<Long, AmenityModel>,
    ): RoomModel {
        val locale = LocaleContextHolder.getLocale()
        return RoomModel(
            id = entity.id,
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
            url = StringUtils.toSlug("/rooms/${entity.id}", entity.title)
        )
    }
}
