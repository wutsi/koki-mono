package com.wutsi.koki.platform.geoip

import com.fasterxml.jackson.annotation.JsonProperty

data class GeoIp(
    val id: String = "",
    val network: String = "",
    val version: String = "",
    val city: String = "",
    val region: String = "",

    @get:JsonProperty("region_code")
    val regionCode: String = "",

    @get:JsonProperty("country_code")
    val countryCode: String = "",

    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val currency: String = "",
)
