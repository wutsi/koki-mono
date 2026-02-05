package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.server.domain.PlaceEntity

interface PlaceContentGenerator {
    fun generate(place: PlaceEntity)
}
