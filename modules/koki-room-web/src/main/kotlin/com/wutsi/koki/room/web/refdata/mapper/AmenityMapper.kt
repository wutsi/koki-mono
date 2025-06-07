package com.wutsi.koki.room.web.refdata.mapper

import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import org.springframework.stereotype.Service

@Service
class AmenityMapper : TenantAwareMapper() {
    companion object {
        val TOP_AMENITIES_ICONS = mapOf(
            1000 to "fa-regular fa-lightbulb", // Electricity
            1001 to "fa-solid fa-droplet", // Running Water
            1003L to "fa-solid fa-snowflake", // Air Conditioning
            1005L to "fa-solid fa-wifi", // Wifi
            1030L to "fa-solid fa-lock", // Locker
            1031L to "fa-solid fa-tv", // TV
            1039L to "fa-solid fa-gamepad", // Video Game Console
            1046L to "fa-solid fa-person-swimming", // Swimming pool
            1052L to "fa-solid fa-umbrella-beach", // Beach access
            1055L to "fa-solid fa-square-parking", // Free Parking
            1059L to "fa-solid fa-square-parking", // Shuttle Service
            1065L to "fa-solid fa-lock", // Smart Lock
        )
    }

    fun toAmenityModel(entity: Amenity): AmenityModel {
        return AmenityModel(
            id = entity.id,
            active = entity.active,
            categoryId = entity.categoryId,
            name = entity.name,
            icon = entity.icon ?: TOP_AMENITIES_ICONS[entity.id],
        )
    }
}
