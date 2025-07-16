package com.wutsi.koki.room.web

import com.wutsi.koki.platform.geoip.GeoIp

object GeoIpFixtures {
    val geoip = GeoIp(
        countryCode = "ca",
        country = "Canada",
        city = "Montreal",
        latitude = 45.508888,
        longitude = -73.561668
    )
}
