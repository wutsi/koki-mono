package com.wutsi.koki.invoice.server.domain

import com.wutsi.koki.invoice.dto.InvoiceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_INVOICE_LOG")
data class InvoiceLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @ManyToOne @JoinColumn(name = "invoice_fk")
    val invoice: InvoiceEntity = InvoiceEntity(),

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    var status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val comment: String? = null,

    val createdAt: Date = Date(),
)
