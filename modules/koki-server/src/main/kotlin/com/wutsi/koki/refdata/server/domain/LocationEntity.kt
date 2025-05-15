package com.wutsi.koki.refdata.server.domain

import com.wutsi.koki.refdata.dto.LocationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_LOCATION")
data class LocationEntity(
    @Id
    val id: Long? = null,

    @Column(name = "parent_fk")
    var parentId: Long? = null,

    var type: LocationType = LocationType.UNKNOWN,
    var name: String = "",
    var asciiName: String = "",
    var country: String = "",
    var population: Long? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
)
