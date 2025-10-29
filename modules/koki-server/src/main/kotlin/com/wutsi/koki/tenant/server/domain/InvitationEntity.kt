package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.InvitationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_INVITATION")
data class InvitationEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    val displayName: String = "",
    val email: String = "",
    var status: InvitationStatus = InvitationStatus.UNKNOWN,
    val type: InvitationType = InvitationType.UNKNOWN,
    val language: String? = null,
    var deleted: Boolean = false,

    val createdAt: Date = Date(),
    var expiresAt: Date = Date(),
    var deletedAt: Date? = null,
    var acceptedAt: Date? = null,
)
