package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service

@Service
class NullContentGenerator : PlaceContentGenerator {
    override fun generate(
        place: PlaceEntity,
        neighbourhood: LocationEntity,
        city: LocationEntity
    ) {
        // DO Nothing
    }
}
