package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import org.springframework.stereotype.Service

@Service
class NullContentGenerator : PlaceContentGenerator {
    override fun generate(place: PlaceEntity) {
        // DO Nothing
    }
}
