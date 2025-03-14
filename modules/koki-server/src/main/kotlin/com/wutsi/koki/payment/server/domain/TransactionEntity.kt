package com.wutsi.koki.payment.server.domain

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TRANSACTION")
data class TransactionEntity(
    @Id val id: String? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1L,

    @Column(name = "created_by_fk") val createdById: Long? = null,

    @Column(name = "invoice_fk") val invoiceId: Long = -1,

    val type: TransactionType = TransactionType.UNKNOWN,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    var status: TransactionStatus = TransactionStatus.UNKNOWN,
    var gateway: PaymentGateway = PaymentGateway.UNKNOWN,
    val amount: Double = 0.0,
    val currency: String = "",
    var checkoutUrl: String? = null,
    var errorCode: String? = null,
    var supplierTransactionId: String? = null,
    var supplierStatus: String? = null,
    var supplierErrorCode: String? = null,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
