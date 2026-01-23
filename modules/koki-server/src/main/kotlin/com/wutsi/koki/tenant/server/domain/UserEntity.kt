package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.Date

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

    @Column(name = "city_fk")
    var cityId: Long? = null,

    @Column(name = "category_fk")
    var categoryId: Long? = null,

    @Column(name = "invitation_fk")
    val invitationId: String? = null,

    val deviceId: String? = null,
    var username: String = "",
    var password: String = "",
    var salt: String = "",
    var status: UserStatus = UserStatus.UNKNOWN,
    var email: String? = null,
    var displayName: String? = null,
    var language: String? = null,
    var employer: String? = null,
    var mobile: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var country: String? = null,
    var photoUrl: String? = null,
    var biography: String? = null,
    var websiteUrl: String? = null,
    var facebookUrl: String? = null,
    var instagramUrl: String? = null,
    var twitterUrl: String? = null,
    var tiktokUrl: String? = null,
    var youtubeUrl: String? = null,
    var street: String? = null,

    @ManyToMany
    @JoinTable(
        name = "T_USER_ROLE",
        joinColumns = arrayOf(JoinColumn(name = "user_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "role_fk")),
    )
    var roles: MutableList<RoleEntity> = mutableListOf(),
)
