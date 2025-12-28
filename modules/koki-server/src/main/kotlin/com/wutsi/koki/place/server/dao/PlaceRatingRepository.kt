package com.wutsi.koki.place.server.dao

import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRatingRepository : CrudRepository<PlaceRatingEntity, Long> {
    fun findByPlaceId(placeId: Long): List<PlaceRatingEntity>
    fun findByPlaceIdAndCriteria(placeId: Long, criteria: RatingCriteria): PlaceRatingEntity?
}
