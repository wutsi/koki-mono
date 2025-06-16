package com.wutsi.koki.refdata.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_AMENITY")
data class AmenityEntity(
    @Id
    val id: Long = 0,

    @Column(name = "category_fk")
    var categoryId: Long = -1,

    var name: String = "",
    var nameFr: String? = null,
    var icon: String? = null,
    var active: Boolean = false,
)
