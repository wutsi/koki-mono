package com.wutsi.koki.place.dto

import java.util.Date

data class Place(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val name: String = "",
    val nameFr: String? = null,
    val type: PlaceType = PlaceType.UNKNOWN,
    val status: PlaceStatus = PlaceStatus.UNKNOWN,

    val summary: String? = null,
    val summaryFr: String? = null,
    val introduction: String? = null,
    val introductionFr: String? = null,
    val description: String? = null,
    val descriptionFr: String? = null,

    val neighbourhoodId: Long? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val websiteURL: String? = null,
    val phoneNumber: String? = null,

    // School-specific fields
    val private: Boolean? = null,
    val international: Boolean? = null,
    val diplomas: List<Diploma> = emptyList(),
    val languages: List<String> = emptyList(),
    val academicSystems: List<String> = emptyList(),
    val faith: Faith? = null,
    val levels: List<SchoolLevel> = emptyList(),

    // Rating
    val rating: Double? = null,
    val ratingCriteria: List<PlaceRating> = emptyList(),

    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
