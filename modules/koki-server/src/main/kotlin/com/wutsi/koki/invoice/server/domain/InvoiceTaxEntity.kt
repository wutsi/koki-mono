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
@Table(name = "T_INVOICE_TAX")
data class InvoiceTaxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "sales_tax_fk")
    val salesTaxId: Long = -1,

    @ManyToOne
    @JoinColumn(name = "invoice_item_fk")
    val invoiceItem: InvoiceItemEntity = InvoiceItemEntity(),

    val rate: Double = 0.0,
    val amount: Double = 0.0,
    val currency: String = "",
)
