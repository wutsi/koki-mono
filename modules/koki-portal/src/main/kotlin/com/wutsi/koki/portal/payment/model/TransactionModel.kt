package com.wutsi.koki.portal.payment.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class TransactionModel(
    val id: String = "",
    val invoice: InvoiceModel = InvoiceModel(),
    val type: TransactionType = TransactionType.UNKNOWN,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val gateway: PaymentGateway = PaymentGateway.UNKNOWN,
    val amount: MoneyModel = MoneyModel(),
    val errorCode: String? = null,
    val supplierTransactionId: String? = null,
    val supplierStatus: String? = null,
    val supplierErrorCode: String? = null,
    val description: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val paymentMethod: PaymentMethodModel = PaymentMethodModel(),
    val createdBy: UserModel? = null,
) {
    val successful: Boolean
        get() = status == TransactionStatus.SUCCESSFUL

    val failed: Boolean
        get() = status == TransactionStatus.FAILED

    val pending: Boolean
        get() = status == TransactionStatus.PENDING
}
