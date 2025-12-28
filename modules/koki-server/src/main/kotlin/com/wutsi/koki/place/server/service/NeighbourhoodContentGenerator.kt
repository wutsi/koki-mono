package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import com.wutsi.koki.place.server.service.ai.NeighborhoodRatingResult
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.place.server.service.ai.RatingCriteraResult
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.util.Date

@Service
class NeighbourhoodContentGenerator(
    private val factory: PlaceAgentFactory,
    private val jsonMapper: JsonMapper,
    private val ratingDao: PlaceRatingRepository,
) : PlaceContentGenerator {
    override fun generate(place: PlaceEntity, neighbourhood: LocationEntity, city: LocationEntity) {
        val agent = factory.createNeighborhoodContentGeneratorAgent(neighbourhood, city)
        val json = agent.run("")
        val result = jsonMapper.readValue(json, NeighbourhoodContentGeneratorResult::class.java)

        // Update place content
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

        // Update ratings
        updateRating(place, RatingCriteria.SECURITY, result.ratings.security)
        updateRating(place, RatingCriteria.INFRASTRUCTURE, result.ratings.infrastructure)
        updateRating(place, RatingCriteria.AMENITIES, result.ratings.amenities)
        updateRating(place, RatingCriteria.LIFESTYLE, result.ratings.lifestyle)
        updateRating(place, RatingCriteria.COMMUTE, result.ratings.commute)
    }

    private fun computeRating(ratings: NeighborhoodRatingResult): Double {
        return listOf(
            ratings.security.value,
            ratings.amenities.value,
            ratings.infrastructure.value,
            ratings.lifestyle.value,
            ratings.commute.value,
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
