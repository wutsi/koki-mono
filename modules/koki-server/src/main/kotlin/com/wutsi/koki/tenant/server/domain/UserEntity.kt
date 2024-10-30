package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_USER")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val email: String = "",
    val password: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val displayName: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
