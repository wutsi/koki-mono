package com.wutsi.koki.place.server.domain

import com.wutsi.koki.place.dto.Faith
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PLACE")
data class PlaceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "hero_image_fk")
    var heroImageId: Long? = null,

    @Column(name = "neighbourhood_fk")
    var neighbourhoodId: Long = -1,

    @Column(name = "city_fk")
    var cityId: Long = -1,

    var name: String = "",
    var asciiName: String = "",
    val type: PlaceType = PlaceType.UNKNOWN,
    var status: PlaceStatus = PlaceStatus.UNKNOWN,
    var summary: String? = null,
    var summaryFr: String? = null,
    var introduction: String? = null,
    var introductionFr: String? = null,
    var description: String? = null,
    var descriptionFr: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null,
    var websiteUrl: String? = null,
    var phoneNumber: String? = null,
    var private: Boolean? = null,
    var international: Boolean? = null,
    var diplomas: String? = null,
    var languages: String? = null,
    var academicSystems: String? = null,
    var faith: Faith? = null,
    var levels: String? = null,
    var rating: Double? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var deleted: Boolean = false,

    @OneToMany
    @JoinColumn(name = "place_fk")
    val ratings: List<PlaceRatingEntity> = emptyList()
)
