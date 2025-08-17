package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PASSWORD_RESET")
data class PasswordResetEntity(
    @Id
    val id: String,

    @ManyToOne
    @JoinColumn("user_fk")
    val user: UserEntity = UserEntity(),

    val createdAt: Date = Date(),
    val expiresAt: Date = Date(),
)
