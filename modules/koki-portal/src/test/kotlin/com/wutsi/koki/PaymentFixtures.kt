package com.wutsi.koki

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethod
import com.wutsi.koki.payment.dto.PaymentMethodCash
import com.wutsi.koki.payment.dto.PaymentMethodCheck
import com.wutsi.koki.payment.dto.PaymentMethodInterac
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.Transaction
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionSummary
import com.wutsi.koki.payment.dto.TransactionType
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
import java.util.UUID

object PaymentFixtures {
    val transactions = listOf(
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[0].id,
            createdAt = InvoiceFixtures.invoices[0].createdAt,
            createdById = InvoiceFixtures.invoices[0].createdById,
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
            createdById = InvoiceFixtures.invoices[1].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.FAILED,
            paymentMethodType = PaymentMethodType.PAYPAL,
            amount = InvoiceFixtures.invoices[1].totalAmount,
            currency = InvoiceFixtures.invoices[1].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[2].id,
            createdAt = InvoiceFixtures.invoices[2].createdAt,
            createdById = InvoiceFixtures.invoices[2].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.SUCCESSFUL,
            paymentMethodType = PaymentMethodType.CREDIT_CARD,
            amount = InvoiceFixtures.invoices[2].totalAmount,
            currency = InvoiceFixtures.invoices[2].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[3].id,
            createdAt = InvoiceFixtures.invoices[3].createdAt,
            createdById = InvoiceFixtures.invoices[3].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.FAILED,
            paymentMethodType = PaymentMethodType.MOBILE,
            amount = InvoiceFixtures.invoices[3].totalAmount,
            currency = InvoiceFixtures.invoices[3].currency,
            errorCode = "INSUFFISANT_FUNDS",
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[3].id,
            createdAt = InvoiceFixtures.invoices[3].createdAt,
            createdById = InvoiceFixtures.invoices[3].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.SUCCESSFUL,
            paymentMethodType = PaymentMethodType.INTERAC,
            amount = InvoiceFixtures.invoices[3].totalAmount,
            currency = InvoiceFixtures.invoices[3].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[3].id,
            createdAt = InvoiceFixtures.invoices[3].createdAt,
            createdById = InvoiceFixtures.invoices[3].createdById,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.PENDING,
            paymentMethodType = PaymentMethodType.BANK,
            amount = InvoiceFixtures.invoices[3].totalAmount,
            currency = InvoiceFixtures.invoices[3].currency,
        ),
        TransactionSummary(
            id = UUID.randomUUID().toString(),
            invoiceId = InvoiceFixtures.invoices[3].id,
            createdAt = InvoiceFixtures.invoices[3].createdAt,
            createdById = InvoiceFixtures.invoices[3].createdById,
            type = TransactionType.REFUND,
            status = TransactionStatus.SUCCESSFUL,
            paymentMethodType = PaymentMethodType.BANK,
            amount = -100.0,
            currency = InvoiceFixtures.invoices[3].currency,
        ),
    )

    val transaction = Transaction(
        id = UUID.randomUUID().toString(),
        invoiceId = InvoiceFixtures.invoice.id,
        createdAt = InvoiceFixtures.invoice.createdAt,
        createdById = InvoiceFixtures.invoice.createdById,
        type = TransactionType.PAYMENT,
        status = TransactionStatus.SUCCESSFUL,
        paymentMethodType = PaymentMethodType.PAYPAL,
        gateway = PaymentGateway.PAYPAL,
        amount = InvoiceFixtures.invoice.totalAmount,
        currency = InvoiceFixtures.invoice.currency,
        description = "This the the description of the transaction",
        errorCode = "INSUFFISANT_FUNDS",
        supplierErrorCode = "111",
        paymentMethod = PaymentMethod(
            cash = PaymentMethodCash(
                collectedAt = Date(),
                collectedById = UserFixtures.users[0].id,
            ),
            interac = PaymentMethodInterac(
                clearedAt = Date(),
                sentAt = DateUtils.addDays(Date(), -3),
                bankName = "DESJARDINS",
                referenceNumber = "1293029-1092019"
            ),
            check = PaymentMethodCheck(
                checkNumber = "135",
                bankName = "CIBC",
//                checkDate = DateUtils.addDays(Date(), -1),
                clearedAt = Date(),
            )
        )
    )
}
