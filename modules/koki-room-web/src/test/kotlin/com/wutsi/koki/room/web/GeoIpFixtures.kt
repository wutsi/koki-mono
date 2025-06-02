package com.wutsi.koki.room.web

import com.wutsi.koki.room.web.location.model.GeoIpModel

object GeoIpFixtures {
    val geoip = GeoIpModel(
        countryCode = "ca",
        country = "Canada",
        city = "Montreal",
        latitude = 45.508888,
        longitude = -73.561668
    )
}
