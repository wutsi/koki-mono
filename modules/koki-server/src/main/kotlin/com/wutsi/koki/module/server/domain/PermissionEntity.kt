package com.wutsi.koki.module.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_PERMISSION")
data class PermissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column("module_fk")
    val moduleId: Long = -1,

    val name: String = "",
    val description: String? = null,
)
