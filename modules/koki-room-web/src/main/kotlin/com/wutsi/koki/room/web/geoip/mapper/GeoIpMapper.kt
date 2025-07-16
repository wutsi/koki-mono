package com.wutsi.koki.room.web.geoip.mapper

import com.wutsi.koki.platform.geoip.GeoIp
import com.wutsi.koki.room.web.geoip.model.GeoIpModel
import org.springframework.stereotype.Service

@Service
class GeoIpMapper {
    fun toGeoIpMapper(entity: GeoIp): GeoIpModel {
        return GeoIpModel(
            countryCode = entity.countryCode,
            country = entity.country,
            city = entity.city,
            id = entity.id,
            currency = entity.currency,
            latitude = entity.latitude,
            longitude = entity.longitude,
            region = entity.region,
            network = entity.network,
            version = entity.version,
            regionCode = entity.regionCode,
        )
    }
}
