package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_USER")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    val email: String = "",
    val password: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val displayName: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),

    @ManyToMany
    @JoinTable(
        name = "T_USER_ROLE",
        joinColumns = arrayOf(JoinColumn(name = "user_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "role_fk")),
    )
    val roles: List<RoleEntity> = emptyList(),
)
