package com.wutsi.koki.payment.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_PAYMENT_METHOD_CHECK")
data class PaymentMethodCheckEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1L,

    @Column(name = "transaction_fk")
    val transactionId: String,

    val checkNumber: String = "",
    val bankName: String = "",
    val clearedAt: Date? = null,
)
