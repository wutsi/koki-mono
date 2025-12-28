package com.wutsi.koki.portal.pub.place.mapper

import com.wutsi.koki.place.dto.Place
import com.wutsi.koki.place.dto.PlaceRating
import com.wutsi.koki.place.dto.PlaceSummary
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.model.PlaceRatingModel
import com.wutsi.koki.refdata.dto.GeoLocation
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class PlaceMapper : TenantAwareMapper() {
    fun toPlaceModel(
        entity: Place,
        images: Map<Long, FileModel> = emptyMap()
    ): PlaceModel {
        val lang = LocaleContextHolder.getLocale().language

        return PlaceModel(
            id = entity.id,
            heroImageUrl = entity.heroImageId?.let { id -> images[id]?.contentUrl },
            name = entity.name,
            type = entity.type,
            status = entity.status,
            summary = if (lang == "fr") {
                entity.summaryFr ?: entity.summary
            } else {
                entity.summary
            },
            introduction = if (lang == "fr") {
                entity.introductionFr ?: entity.introduction
            } else {
                entity.introduction
            },
            description = if (lang == "fr") {
                entity.descriptionFr ?: entity.description
            } else {
                entity.description
            },
            neighbourhoodId = entity.neighbourhoodId,
            geoLocation = toGeoLocation(entity),
            websiteURL = entity.websiteURL,
            phoneNumber = entity.phoneNumber,
            private = entity.private,
            international = entity.international,
            diplomas = entity.diplomas,
            languages = entity.languages,
            academicSystems = entity.academicSystems,
            faith = entity.faith,
            levels = entity.levels,
            rating = entity.rating,
            ratingCriteria = entity.ratingCriteria.map { toPlaceRatingModel(it) },
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toPlaceModel(
        entity: PlaceSummary,
        images: Map<Long, FileModel> = emptyMap()
    ): PlaceModel {
        val lang = LocaleContextHolder.getLocale().language

        return PlaceModel(
            id = entity.id,
            heroImageUrl = entity.heroImageId?.let { id -> images[id]?.contentUrl },
            name = entity.name,
            type = entity.type,
            summary = if (lang == "fr") {
                entity.summaryFr ?: entity.summary
            } else {
                entity.summary
            },
            neighbourhoodId = entity.neighbourhoodId,
            rating = entity.rating,
        )
    }

    private fun toGeoLocation(entity: Place): GeoLocation? {
        return if (entity.latitude != null && entity.longitude != null) {
            GeoLocation(
                latitude = entity.latitude!!,
                longitude = entity.longitude!!
            )
        } else {
            null
        }
    }

    private fun toPlaceRatingModel(entity: PlaceRating): PlaceRatingModel {
        return PlaceRatingModel(
            criteria = entity.criteria,
            value = entity.value,
            reason = entity.reason,
        )
    }
}
