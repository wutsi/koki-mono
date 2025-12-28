package com.wutsi.koki.portal.pub.place.model

import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.Faith
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.refdata.dto.GeoLocation
import java.util.Date

data class PlaceModel(
    val id: Long = -1,
    val heroImageUrl: String? = null,
    val name: String = "",
    val type: PlaceType = PlaceType.UNKNOWN,
    val status: PlaceStatus = PlaceStatus.UNKNOWN,
    val summary: String? = null,
    val introduction: String? = null,
    val description: String? = null,
    val neighbourhoodId: Long = -1,
    val cityId: Long = -1,
    val geoLocation: GeoLocation? = null,
    val websiteURL: String? = null,
    val phoneNumber: String? = null,
    val private: Boolean? = null,
    val international: Boolean? = null,
    val diplomas: List<Diploma> = emptyList(),
    val languages: List<String> = emptyList(),
    val academicSystems: List<String> = emptyList(),
    val faith: Faith? = null,
    val levels: List<SchoolLevel> = emptyList(),
    val rating: Double? = null,
    val ratingCriteria: List<PlaceRatingModel> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
) {
    val introductionHtml: String?
        get() = introduction?.let { text -> HtmlUtils.toHtml(text) }

    val descriptionHtml: String?
        get() = description?.let { text -> HtmlUtils.toHtml(text) }

    val ratingPercentage: Int?
        get() = rating?.let { rating -> (100.0 * rating / 5.0).toInt() }

    val ratingCode: String?
        get() = rating?.let { rating ->
            if (rating < 2.0) {
                "danger"
            } else if (rating < 3.5) {
                "warning"
            } else if (rating < 4.5) {
                "info"
            } else {
                "success"
            }
        }

    val ratingText: String?
        get() = rating?.let { "%.1f".format(rating) }
}
