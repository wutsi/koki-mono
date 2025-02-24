package com.wutsi.koki.invoice.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "T_INVOICE_ITEM")
data class InvoiceItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "product_fk")
    val productId: Long = -1,

    @Column(name = "unit_price_fk")
    val unitPriceId: Long = -1,

    @ManyToOne
    @JoinColumn(name = "invoice_fk")
    val invoice: InvoiceEntity = InvoiceEntity(),

    val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val subTotal: Double = 0.0,
    val description: String? = null,
    val currency: String = "",
)
