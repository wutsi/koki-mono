package com.wutsi.koki.place.server.domain

import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.Faith
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.place.server.domain.converter.DiplomaListConverter
import com.wutsi.koki.place.server.domain.converter.SchoolLevelListConverter
import com.wutsi.koki.place.server.domain.converter.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
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

    @Column(name = "hero_image_url")
    var heroImageUrl: String? = null,

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

    @Convert(converter = DiplomaListConverter::class)
    @Column(columnDefinition = "TEXT")
    var diplomas: List<Diploma>? = null,

    @Convert(converter = StringListConverter::class)
    @Column(columnDefinition = "TEXT")
    var languages: List<String>? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "academic_systems", columnDefinition = "TEXT")
    var academicSystems: List<String>? = null,

    var faith: Faith? = null,

    @Convert(converter = SchoolLevelListConverter::class)
    @Column(columnDefinition = "TEXT")
    var levels: List<SchoolLevel>? = null,

    var rating: Double? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var deleted: Boolean = false,

    @OneToMany
    @JoinColumn(name = "place_fk")
    val ratings: List<PlaceRatingEntity> = emptyList()
)
