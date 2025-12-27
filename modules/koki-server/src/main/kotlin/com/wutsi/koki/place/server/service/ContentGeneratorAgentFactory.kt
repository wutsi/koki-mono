package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceType
import org.springframework.stereotype.Service

@Service
class ContentGeneratorAgentFactory(private val neighborhood: NeighbourhoodContentGenerator) {
    fun get(request: CreatePlaceRequest): PlaceContentGenerator {
        when (request.type) {
            PlaceType.NEIGHBORHOOD -> return neighborhood
            else -> throw IllegalArgumentException("Unsupported place type: ${request.type}")
        }
    }
}
