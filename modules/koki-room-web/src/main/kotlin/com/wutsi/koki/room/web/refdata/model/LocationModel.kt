package com.wutsi.koki.room.web.refdata.model

import com.wutsi.koki.refdata.dto.LocationType

data class LocationModel(
    val id: Long = -1,
    val parentId: Long? = null,
    val name: String = "",
    val type: LocationType = LocationType.UNKNOWN,
    val country: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    val url: String = "",
) {
    val hasGeoLocation: Boolean
        get() = latitude != null && longitude != null
}
