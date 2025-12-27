package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class NeighbourhoodContentGenerator(val locationService: LocationService) : PlaceContentGenerator {
    override fun generate(request: CreatePlaceRequest): PlaceEntity {
        val neighbourhood = request
    }
}
