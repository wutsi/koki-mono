package com.wutsi.koki.product.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PRICE")
data class PriceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "product_fk")
    val productId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    var name: String = "",
    var amount: Double = 0.0,
    var currency: String = "",
    var startAt: Date? = null,
    var endAt: Date? = null,
    var active: Boolean = true,
    var deleted: Boolean = false,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
