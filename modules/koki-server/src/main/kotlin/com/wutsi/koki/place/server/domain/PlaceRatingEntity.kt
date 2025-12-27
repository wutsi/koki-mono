package com.wutsi.koki.place.server.domain

import com.wutsi.koki.place.dto.RatingCriteria
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_PLACE_RATING")
data class PlaceRatingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "place_fk")
    val placeId: Long = -1,

    var criteria: RatingCriteria = RatingCriteria.UNKNOWN,
    var value: Int = 0,
    var reason: String? = null,
)
