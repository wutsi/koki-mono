package com.wutsi.koki.place.server.mapper

import com.wutsi.koki.place.dto.Place
import com.wutsi.koki.place.dto.PlaceRating
import com.wutsi.koki.place.dto.PlaceSummary
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import org.springframework.stereotype.Service

@Service
class PlaceMapper {
    fun toPlace(entity: PlaceEntity): Place {
        return Place(
            id = entity.id ?: -1,
            heroImageUrl = entity.heroImageUrl,
            name = entity.name,
            type = entity.type,
            status = entity.status,

            summary = entity.summary,
            summaryFr = entity.summaryFr,
            introduction = entity.introduction,
            introductionFr = entity.introductionFr,
            description = entity.description,
            descriptionFr = entity.descriptionFr,

            neighbourhoodId = entity.neighbourhoodId,
            cityId = entity.cityId,
            longitude = entity.longitude,
            latitude = entity.latitude,
            websiteURL = entity.websiteUrl,
            phoneNumber = entity.phoneNumber,

            // School-specific fields
            private = entity.private,
            international = entity.international,
            diplomas = entity.diplomas ?: emptyList(),
            languages = entity.languages ?: emptyList(),
            academicSystems = entity.academicSystems ?: emptyList(),
            faith = entity.faith,
            levels = entity.levels ?: emptyList(),

            // Rating
            rating = entity.rating,
            ratingCriteria = entity.ratings.map { toPlaceRating(it) },

            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toPlaceSummary(entity: PlaceEntity): PlaceSummary {
        return PlaceSummary(
            id = entity.id ?: -1,
            heroImageUrl = entity.heroImageUrl,
            neighbourhoodId = entity.neighbourhoodId,
            cityId = entity.cityId,
            type = entity.type,
            name = entity.name,
            summary = entity.summary,
            summaryFr = entity.summaryFr,
            introduction = entity.introduction,
            introductionFr = entity.introductionFr,
            status = entity.status,
            rating = entity.rating,
        )
    }

    fun toPlaceRating(entity: PlaceRatingEntity): PlaceRating {
        return PlaceRating(
            criteria = entity.criteria,
            value = entity.value,
            reason = entity.reason,
        )
    }
}
