package com.wutsi.koki.room.server.domain

import com.wutsi.koki.refdata.server.domain.LocationEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ROOM_LOCATION_METRIC")
data class RoomLocationMetricEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @ManyToOne
    @JoinColumn("location_fk")
    val location: LocationEntity = LocationEntity(),

    val totalPublishedRentals: Int = 0,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
