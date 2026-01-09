package com.wutsi.koki.portal.pub.place.service

import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.place.mapper.PlaceMapper
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.sdk.KokiPlaces
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val koki: KokiPlaces,
    private val mapper: PlaceMapper,
) {
    fun get(id: Long, fullGraph: Boolean = true): PlaceModel {
        val place = koki.get(id).place
        return mapper.toPlaceModel(entity = place)
    }

    fun search(
        neighbourhoodIds: List<Long> = emptyList(),
        cityIds: List<Long> = emptyList(),
        types: List<PlaceType> = emptyList(),
        statuses: List<PlaceStatus> = emptyList(),
        keyword: String? = null,
        minRating: Double? = null,
        maxRating: Double? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<PlaceModel> {
        val places = koki.search(
            neighbourhoodIds = neighbourhoodIds,
            cityIds = cityIds,
            types = types,
            statuses = statuses,
            keyword = keyword,
            minRating = minRating,
            maxRating = maxRating,
            limit = limit,
            offset = offset,
        ).places

        return places.map { place -> mapper.toPlaceModel(entity = place) }
    }
}
