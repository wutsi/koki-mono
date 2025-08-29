package com.wutsi.koki.portal.refdata.model

data class GeoLocationModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    val url: String
        get() = "https://maps.google.com/maps?t=m&z=13&q=loc:$latitude+$longitude"
}
