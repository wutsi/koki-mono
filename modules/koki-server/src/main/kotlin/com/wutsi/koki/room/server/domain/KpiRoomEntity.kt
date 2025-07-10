package com.wutsi.koki.room.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_KPI_ROOM")
data class KpiRookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long? = null,

    @Column(name = "room_fk")
    val roomId: Long? = null,

    val period: Date = Date(),
    val totalImpressions: Long = 0L,
    val totalViews: Long = 0L,
    val totalClicks: Long = 0L,
    val totalMessages: Long = 0L,
    val totalVisitors: Long = 0L,
    val ctr: Double = 0.0,
    val cvr: Double = 0.0,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
