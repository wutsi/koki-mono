package com.wutsi.koki.tenant.server.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_CONFIGURATION")
data class ConfigurationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "attribute_fk")
    val attribute: AttributeEntity = AttributeEntity(),

    var value: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
