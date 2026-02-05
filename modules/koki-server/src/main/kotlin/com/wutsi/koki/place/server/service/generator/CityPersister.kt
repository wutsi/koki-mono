package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ai.CityContentGeneratorResult
import com.wutsi.koki.refdata.server.domain.LocationEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class CityPersister(
    private val dao: PlaceRepository,
) {
    @Transactional
    fun persist(
        place: PlaceEntity,
        city: LocationEntity,
        result: CityContentGeneratorResult,
    ) {
        place.latitude = city.latitude
        place.longitude = city.longitude
        place.summary = result.summary
        place.introduction = result.introduction
        place.description = result.description
        place.summaryFr = result.summaryFr
        place.introductionFr = result.introductionFr
        place.descriptionFr = result.descriptionFr
        place.modifiedAt = Date()
        dao.save(place)
    }
}
