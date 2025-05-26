package com.wutsi.koki.room.web.refdata.mapper

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.refdata.model.AddressModel
import com.wutsi.koki.room.web.refdata.model.LocationModel
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class LocationMapper : TenantAwareMapper() {
    fun toLocationModel(entity: Location): LocationModel {
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
            longitude = entity.longitude,
            latitude = entity.latitude,
        )
    }

    fun toAddressModel(entity: Address, locations: Map<Long, LocationModel>): AddressModel {
        val city = entity.cityId?.let { id -> locations[id] }
        return AddressModel(
            street = entity.street?.ifEmpty { null },
            postalCode = entity.postalCode?.ifEmpty { null },
            city = city,
            state = city?.parentId?.let { id -> locations[id] },
            country = entity.country?.ifEmpty { null },
            countryName = entity.country?.let { country ->
                Locale(LocaleContextHolder.getLocale().language, country).getDisplayCountry()
            }
        )
    }
}
