package com.wutsi.koki.portal.pub.refdata.model

import com.wutsi.koki.refdata.dto.LocationType

data class LocationModel(
    val id: Long = -1,
    val parentId: Long? = null,
    val name: String = "",
    val type: LocationType = LocationType.UNKNOWN,
    val country: String = "",
    val geoLocation: GeoLocationModel? = null,
    val url: String = "",
)
