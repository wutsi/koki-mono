package com.wutsi.koki.portal.pub.place.service

import com.wutsi.koki.place.dto.PlaceSort
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
        sort: PlaceSort? = null,
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
            sort = sort,
            limit = limit,
            offset = offset,
        ).places

        return places.map { place -> mapper.toPlaceModel(entity = place) }
    }

    fun similarNeighbourhoods(neighbourhood: PlaceModel, limit: Int): List<PlaceModel> {
        val rating = neighbourhood.rating ?: 0.0
        val minRating = if (rating.toInt() >= 4) 4.0 else rating - .25
        val maxRating = if (rating.toInt() >= 4) null else rating + .25

        return search(
            cityIds = listOf(neighbourhood.cityId),
            types = listOf(PlaceType.NEIGHBORHOOD),
            statuses = listOf(PlaceStatus.PUBLISHED),
            minRating = minRating,
            maxRating = maxRating,
            sort = PlaceSort.RATING_HIGH_LOW,
            limit = limit,
        ).filter { similar -> similar.id != neighbourhood.id }
    }
}
