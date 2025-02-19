package com.wutsi.koki.tax.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TAX_PRODUCT")
data class TaxProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "tax_fk")
    val taxId: Long = -1,

    @Column(name = "product_fk")
    var productId: Long = -1,

    @Column(name = "unit_price_fk")
    var unitPriceId: Long = -1,

    var unitPrice: Double = 0.0,
    var quantity: Int = 1,
    var description: String? = null,
    var currency: String = "",

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
) {
    var subTotal: Double
        get() = quantity * unitPrice
        set(value) {}
}
