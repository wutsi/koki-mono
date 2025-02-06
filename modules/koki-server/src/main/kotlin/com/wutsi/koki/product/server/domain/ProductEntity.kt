package com.wutsi.koki.product.server.domain

import com.wutsi.koki.product.dto.ProductType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PRODUCT")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    var name: String = "",
    var code: String? = null,
    var description: String? = null,
    var type: ProductType = ProductType.UNKNOWN,
    var active: Boolean = true,
    var deleted: Boolean = false,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
