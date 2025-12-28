package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import org.springframework.stereotype.Service

@Service
class PlaceRatingService(
    private val dao: PlaceRatingRepository,
) {
    fun save(place: PlaceEntity, criteria: RatingCriteria, value: Int, reason: String?): PlaceRatingEntity {
        val rating = dao.findByPlaceIdAndCriteria(place.id!!, criteria)
        return if (rating == null) {
            dao.save(
                PlaceRatingEntity(
                    placeId = place.id,
                    criteria = criteria,
                    value = value,
                    reason = reason,
                )
            )
        } else {
            rating.value = value
            rating.reason = reason
            dao.save(rating)
        }
    }
}
