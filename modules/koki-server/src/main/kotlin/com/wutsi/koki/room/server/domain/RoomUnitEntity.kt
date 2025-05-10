package com.wutsi.koki.room.server.domain

import com.wutsi.koki.room.dto.RoomUnitStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ROOM_UNIT")
data class RoomUnitEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deleteById: Long? = null,

    @Column(name = "room_fk")
    val roomId: Long = -1,

    var number: String = "",
    var floor: Int = 0,
    var status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
    var deleted: Boolean = false,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
