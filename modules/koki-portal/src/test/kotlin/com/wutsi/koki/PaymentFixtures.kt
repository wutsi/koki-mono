package com.wutsi.koki

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethod
import com.wutsi.koki.payment.dto.PaymentMethodCash
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.Transaction
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.payment.dto.TransactionType
import java.util.Date
import java.util.UUID

object TransactionFixtures {
    val transactions = listOf(
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[0].id,
            createdAt = InvoiceFixtures.invoices[0].createdAt,
            createById = InvoiceFixtures.invoices[0].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.SUCCESSFUL,
            paymentMethodType = PaymentMethodType.CASH,
            amount = InvoiceFixtures.invoices[0].totalAmount,
            currency = InvoiceFixtures.invoices[0].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[1].id,
            createdAt = InvoiceFixtures.invoices[1].createdAt,
            createById = InvoiceFixtures.invoices[1].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.FAILED,
            paymentMethodType = PaymentMethodType.CASH,
            amount = InvoiceFixtures.invoices[1].totalAmount,
            currency = InvoiceFixtures.invoices[1].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[2].id,
            createdAt = InvoiceFixtures.invoices[2].createdAt,
            createById = InvoiceFixtures.invoices[2].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.SUCCESSFUL,
            paymentMethodType = PaymentMethodType.CASH,
            amount = InvoiceFixtures.invoices[2].totalAmount,
            currency = InvoiceFixtures.invoices[2].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[3].id,
            createdAt = InvoiceFixtures.invoices[3].createdAt,
            createById = InvoiceFixtures.invoices[3].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.FAILED,
            paymentMethodType = PaymentMethodType.CASH,
            amount = InvoiceFixtures.invoices[3].totalAmount,
            currency = InvoiceFixtures.invoices[3].currency,
            errorCode = "INSUFFISANT_FUNDS",
        ),
    )

    val transaction = Transaction(
        id = UUID.randomUUID().toString(),
        invoiceId = InvoiceFixtures.invoice.id,
        createdAt = InvoiceFixtures.invoice.createdAt,
        createById = InvoiceFixtures.invoice.createdById,
        type = TransactionType.PAYMENT,
        status = TransactionStatus.SUCCESSFUL,
        paymentMethodType = PaymentMethodType.CASH,
        amount = InvoiceFixtures.invoice.totalAmount,
        currency = InvoiceFixtures.invoice.currency,
        description = "This the the description of the transaction",
        errorCode = "INSUFFISANT_FUNDS",
        gateway = PaymentGateway.UNKNOWN,
        supplierErrorCode = "111",
        paymentMethod = PaymentMethod(
            cash = PaymentMethodCash(
                collectedAt = Date(),
                collectedById = UserFixtures.users[0].id,
            ),
        )
    )
}
