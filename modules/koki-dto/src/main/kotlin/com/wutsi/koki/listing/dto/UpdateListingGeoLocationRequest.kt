package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.GeoLocation

data class UpdateListingGeoLocationRequest(
    val geoLocation: GeoLocation? = null
)
