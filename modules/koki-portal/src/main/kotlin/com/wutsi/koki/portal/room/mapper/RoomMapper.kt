package com.wutsi.koki.portal.lodging.mapper

import com.wutsi.koki.lodging.dto.RoomSummary
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.lodging.model.RoomModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import org.springframework.stereotype.Service

@Service
class RoomMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toRoomModel(
        entity: RoomSummary,
        locations: Map<Long, LocationModel>
    ): RoomModel {
        return RoomModel(
            id = entity.id ?: -1,
            type = entity.type,
            status = entity.status,
            title = entity.title,
            numberOfRooms = entity.numberOfRooms,
            numberOfBathrooms = entity.numberOfBathrooms,
            numberOfBeds = entity.numberOfBeds,
            maxGuests = entity.maxGuests,
            address = AddressModel(
                city = entity.address.cityId?.let { id -> locations[id] },
                state = entity.address.stateId?.let { id -> locations[id] },
                street = entity.address.street,
                postalCode = entity.address.postalCode,
                country = entity.address.country,
            ),
            pricePerNight = moneyMapper.toMoneyModel(entity.pricePerNight.amount, entity.pricePerNight.currency),
        )
    }
}
