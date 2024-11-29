package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_CONFIGURATION")
data class ConfigurationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    val name: String = "",
    var value: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
