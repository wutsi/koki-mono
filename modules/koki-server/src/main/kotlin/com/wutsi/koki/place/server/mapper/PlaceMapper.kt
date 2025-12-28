package com.wutsi.koki.place.server.mapper

import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.Place
import com.wutsi.koki.place.dto.PlaceRating
import com.wutsi.koki.place.dto.PlaceSummary
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import org.springframework.stereotype.Service

@Service
class PlaceMapper {
    fun toPlace(entity: PlaceEntity): Place {
        return Place(
            id = entity.id ?: -1,
            heroImageId = entity.heroImageId,
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
            longitude = entity.longitude,
            latitude = entity.latitude,
            websiteURL = entity.websiteUrl,
            phoneNumber = entity.phoneNumber,

            // School-specific fields
            private = entity.private,
            international = entity.international,
            diplomas = parseDiplomas(entity.diplomas),
            languages = parseLanguages(entity.languages),
            academicSystems = parseAcademicSystems(entity.academicSystems),
            faith = entity.faith,
            levels = parseSchoolLevels(entity.levels),

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
            heroImageId = entity.heroImageId,
            neighbourhoodId = entity.neighbourhoodId,
            type = entity.type,
            name = entity.name,
            summary = entity.summary,
            summaryFr = entity.summaryFr,
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

    // Parse comma-separated diploma list
    fun parseDiplomas(csv: String?): List<Diploma> {
        if (csv.isNullOrBlank()) {
            return emptyList()
        }
        return csv.split(",")
            .map { it.trim() }
            .mapNotNull {
                try {
                    Diploma.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            }
    }

    // Parse comma-separated school level list
    fun parseSchoolLevels(csv: String?): List<SchoolLevel> {
        if (csv.isNullOrBlank()) {
            return emptyList()
        }
        return csv.split(",")
            .map { it.trim() }
            .mapNotNull {
                try {
                    SchoolLevel.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            }
    }

    // Parse comma-separated language codes
    fun parseLanguages(csv: String?): List<String> {
        if (csv.isNullOrBlank()) {
            return emptyList()
        }
        return csv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Parse comma-separated academic system codes (country codes)
    fun parseAcademicSystems(csv: String?): List<String> {
        if (csv.isNullOrBlank()) {
            return emptyList()
        }
        return csv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Serialize diploma list to comma-separated string
    fun serializeDiplomas(list: List<Diploma>): String? {
        if (list.isEmpty()) {
            return null
        }
        return list.joinToString(",") { it.name }
    }

    // Serialize school level list to comma-separated string
    fun serializeSchoolLevels(list: List<SchoolLevel>): String? {
        if (list.isEmpty()) {
            return null
        }
        return list.joinToString(",") { it.name }
    }

    // Serialize language codes to comma-separated string
    fun serializeLanguages(list: List<String>): String? {
        if (list.isEmpty()) {
            return null
        }
        return list.joinToString(",")
    }

    // Serialize academic system codes to comma-separated string
    fun serializeAcademicSystems(list: List<String>): String? {
        if (list.isEmpty()) {
            return null
        }
        return list.joinToString(",")
    }
}
