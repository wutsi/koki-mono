package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PASSWORD_RESET_TOKEN")
data class PasswordResetTokenEntity(
    @Id
    val id: String? = null,

    @ManyToOne
    @JoinColumn("user_fk")
    val user: UserEntity = UserEntity(),

    @Column("tenant_fk")
    val tenantId: Long = -1,
    val createdAt: Date = Date(),
    var expiresAt: Date = Date(),
)
