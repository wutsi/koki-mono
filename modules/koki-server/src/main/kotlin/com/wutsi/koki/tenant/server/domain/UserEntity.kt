package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date
import kotlin.collections.mutableListOf

@Entity
@Table(name = "T_USER")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    var email: String = "",
    val password: String = "",
    val salt: String = "",
    var status: UserStatus = UserStatus.ACTIVE,
    var type: UserType = UserType.UNKNOWN,
    var displayName: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),

    @BatchSize(20)
    @ManyToMany
    @JoinTable(
        name = "T_USER_ROLE",
        joinColumns = arrayOf(JoinColumn(name = "user_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "role_fk")),
    )
    var roles: MutableList<RoleEntity> = mutableListOf(),
)
