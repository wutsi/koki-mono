package com.wutsi.koki.place.server.domain

import com.wutsi.koki.place.dto.Faith
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PLACE")
data class PlaceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "hero_image_fk")
    var heroImageId: Long? = null,

    @Column(name = "neighbourhood_fk")
    var neighbourhoodId: Long? = null,

    var name: String = "",

    @Column(name = "name_fr")
    var nameFr: String? = null,

    var type: PlaceType = PlaceType.UNKNOWN,
    var status: PlaceStatus = PlaceStatus.UNKNOWN,

    var summary: String? = null,

    @Column(name = "summary_fr")
    var summaryFr: String? = null,

    var introduction: String? = null,

    @Column(name = "introduction_fr")
    var introductionFr: String? = null,

    var description: String? = null,

    @Column(name = "description_fr")
    var descriptionFr: String? = null,

    var longitude: Double? = null,
    var latitude: Double? = null,

    @Column(name = "website_url")
    var websiteURL: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    // School-specific fields
    var private: Boolean? = null,
    var international: Boolean? = null,
    var diplomas: String? = null,
    var languages: String? = null,

    @Column(name = "academic_systems")
    var academicSystems: String? = null,

    var faith: Faith? = null,
    var levels: String? = null,

    var rating: Double? = null,

    @Column(name = "created_at")
    val createdAt: Date = Date(),

    @Column(name = "modified_at")
    var modifiedAt: Date = Date(),

    @Column(name = "deleted_at")
    var deletedAt: Date? = null,

    var deleted: Boolean = false,
)
