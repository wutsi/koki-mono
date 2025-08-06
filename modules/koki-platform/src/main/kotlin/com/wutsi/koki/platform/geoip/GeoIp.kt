package com.wutsi.koki.platform.geoip

data class GeoIp(
    val id: String = "",
    val network: String = "",
    val version: String = "",
    val city: String = "",
    val region: String = "",
    val regionCode: String = "",
    val countryCode: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val currency: String = "",
)
