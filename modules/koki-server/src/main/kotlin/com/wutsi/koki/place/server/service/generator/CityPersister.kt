package com.wutsi.koki.place.server.service.generator

import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import com.wutsi.koki.place.server.service.ai.NeighborhoodRatingResult
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.RatingCriteraResult
import com.wutsi.koki.refdata.server.domain.LocationEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class NeighbourhoodPersister(
    private val dao: PlaceRepository,
    private val ratingDao: PlaceRatingRepository,
) {
    @Transactional
    fun persist(
        place: PlaceEntity,
        neighbourhood: LocationEntity,
        result: NeighbourhoodContentGeneratorResult,
    ) {
        place.latitude = neighbourhood.latitude
        place.longitude = neighbourhood.longitude
        place.summary = result.summary
        place.introduction = result.introduction
        place.description = result.description
        place.summaryFr = result.summaryFr
        place.introductionFr = result.introductionFr
        place.descriptionFr = result.descriptionFr
        place.rating = computeRating(result.ratings)
        place.modifiedAt = Date()
        dao.save(place)

        // Update ratings
        updateRating(place, RatingCriteria.SECURITY, result.ratings.security)
        updateRating(place, RatingCriteria.EDUCATION, result.ratings.education)
        updateRating(place, RatingCriteria.INFRASTRUCTURE, result.ratings.infrastructure)
        updateRating(place, RatingCriteria.COMMUTE, result.ratings.commute)
        updateRating(place, RatingCriteria.AMENITIES, result.ratings.amenities)
    }

    private fun computeRating(ratings: NeighborhoodRatingResult): Double {
        return listOf(
            ratings.security.value,
            ratings.amenities.value,
            ratings.infrastructure.value,
            ratings.commute.value,
            ratings.education.value,
        ).average()
    }

    private fun updateRating(
        place: PlaceEntity,
        criteria: RatingCriteria,
        result: RatingCriteraResult
    ) {
        val rating = ratingDao.findByPlaceIdAndCriteria(place.id!!, criteria)
        if (rating == null) {
            ratingDao.save(
                PlaceRatingEntity(
                    placeId = place.id,
                    criteria = criteria,
                    value = result.value,
                    reason = result.reason,
                )
            )
        } else {
            rating.value = result.value
            rating.reason = result.reason
            ratingDao.save(rating)
        }
    }
}
