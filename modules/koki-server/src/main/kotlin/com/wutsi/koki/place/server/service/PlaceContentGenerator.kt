package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity

interface PlaceContentGenerator {
    fun generate(place: PlaceEntity, neighbourhood: LocationEntity, city: LocationEntity)
}
