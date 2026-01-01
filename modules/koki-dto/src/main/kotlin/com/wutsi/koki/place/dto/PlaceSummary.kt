package com.wutsi.koki.place.dto

data class PlaceSummary(
    val id: Long = -1,
    val heroImageUrl: String? = null,
    val neighbourhoodId: Long = -1,
    val cityId: Long = -1,
    val type: PlaceType = PlaceType.UNKNOWN,
    val status: PlaceStatus = PlaceStatus.UNKNOWN,
    val name: String = "",
    val summary: String? = null,
    val summaryFr: String? = null,
    val introduction: String? = null,
    val introductionFr: String? = null,
    val rating: Double? = null,
    val diplomas: List<Diploma> = emptyList(),
    val languages: List<String> = emptyList(),
    val academicSystems: List<String> = emptyList(),
    val levels: List<SchoolLevel> = emptyList(),
    val faith: Faith? = null,
    val websiteUrl: String? = null,
    val private: Boolean? = null,
    val international: Boolean? = null,
)
