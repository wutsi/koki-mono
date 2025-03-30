package com.wutsi.koki.payment.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PAYMENT_METHOD_INTERACT")
data class PaymentMethodInteractEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1L,

    @Column(name = "transaction_fk")
    val transactionId: String,

    val referenceNumber: String = "",
    val bankName: String = "",
    val sentAt: Date? = null,
    val clearedAt: Date? = null,
)
