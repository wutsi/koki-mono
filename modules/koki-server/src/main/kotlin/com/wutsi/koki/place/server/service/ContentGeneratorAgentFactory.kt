package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.service.generator.NeighbourhoodContentGenerator
import com.wutsi.koki.place.server.service.generator.NullContentGenerator
import org.springframework.stereotype.Service

@Service
class ContentGeneratorAgentFactory(private val neighborhood: NeighbourhoodContentGenerator) {
    companion object {
        private val NULL = NullContentGenerator()
    }

    fun get(type: PlaceType): PlaceContentGenerator {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> neighborhood
            else -> NULL
        }
    }
}
