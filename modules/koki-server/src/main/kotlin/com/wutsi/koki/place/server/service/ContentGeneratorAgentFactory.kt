package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.PlaceType
import org.springframework.stereotype.Service

@Service
class ContentGeneratorAgentFactory(private val neighborhood: NeighbourhoodContentGenerator) {
    fun get(type: PlaceType): PlaceContentGenerator {
        when (type) {
            PlaceType.NEIGHBORHOOD -> return neighborhood
            else -> throw IllegalArgumentException("Unsupported place type: $type")
        }
    }
}
